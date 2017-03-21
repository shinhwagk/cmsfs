package org.wex.cmsfs.collect.ssh.impl

import java.io.{BufferedReader, InputStreamReader}
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.jcraft.jsch.{ChannelExec, JSch}
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.core.CollectCore
import org.wex.cmsfs.common.CmsfsAkkaStream
import org.wex.cmsfs.monitor.api.{CollectResult, MonitorService}
import play.api.Configuration
import play.api.libs.json.Json
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

class Collecting(ct: CollectTopic,
                 ms: MonitorService,
                 override val config: Configuration,
                 system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CollectCore {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val source = ct.CollectTopic.subscriber

  source.map(elem => loggerFlow(elem, s"receive ssh collect ${elem.connector.id}"))
    .mapAsync(10) { cis =>

      val monitorDetailId = cis.monitorDetailId
      val ip = cis.connector.ip
      val user = cis.connector.user
      val metricName = cis.collect.name
      val port = cis.connector.port
      val dbName = cis.connector.name
      val utcDate = cis.utcDate
      val path = cis.collect.path

      collectAction(ip, user, genUrl(path), Some(port))
        .filter(_.isDefined)
        .map(rs => (monitorDetailId, metricName, rs, utcDate, dbName))
    }.withAttributes(supervisionStrategy((x => "x")))
    .mapAsync(10) {
      case (monitorDetailId, metricName, rsOpt, utcDate, dbName) =>
        ms.pushCollectResult.invoke(CollectResult(monitorDetailId, metricName, rsOpt, utcDate, dbName)).map(_ => monitorDetailId)
    }.withAttributes(supervisionStrategy((x => "x")))
    .runWith(Sink.foreach(id => logger.info(s"id:${id}, collect success.")))

  def collectAction(host: String, user: String, scriptUrl: String, port: Option[Int] = Some(22)): Future[Option[String]] = Future {
    val OSName = System.getProperty("os.name").toLowerCase();
    try {
      if (OSName.startsWith("win")) {
        // test
        Some(ssh("C:\\Users\\zhangxu\\.ssh\\id_rsa", user, host, scriptUrl, port.get));
      } else if (OSName == "linux") {
        Some(ssh("~/.ssh/id_rsa", user, host, scriptUrl, port.get));
      } else {
        logger.error("OS not match..");
        None
      }
    } catch {
      case ex: Exception => logger.error(ex.getMessage + " collectionAction"); None
    }
  }

  def ssh(keyPath: String, user: String, host: String, scriptUrl: String, port: Int): String = {
    val jsch = new JSch();
    jsch.addIdentity(keyPath);
    val session = jsch.getSession(user, host, port);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    val channelExec: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    val in = channelExec.getInputStream();
    channelExec.setCommand(s"curl -s ${scriptUrl} | sh");
    channelExec.connect();

    val reader = new BufferedReader(new InputStreamReader(in));

    val rs = new ArrayBuffer[String]()

    var line: Option[String] = Option(reader.readLine())

    while (line.isDefined) {
      rs += line.get
      line = Option(reader.readLine())
    }

    val exitStatus: Int = channelExec.getExitStatus();

    channelExec.disconnect();
    session.disconnect();

    if (exitStatus < 0) {
      // System.out.println("Done, but exit status not set!");
    } else if (exitStatus > 0) {
      // System.out.println("Done, but with error!");
    } else {
      // System.out.println("Done!");
    }
    Json.toJson(rs).toString()
  }

}
