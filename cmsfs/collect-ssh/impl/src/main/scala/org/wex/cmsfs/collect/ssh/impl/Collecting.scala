package org.wex.cmsfs.collect.ssh.impl

import java.io.{BufferedReader, InputStreamReader}

import akka.stream.scaladsl.Sink
import akka.stream.{ActorAttributes, Materializer, Supervision}
import com.jcraft.jsch.{ChannelExec, JSch}
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.monitor.api.{CollectResult, MonitorService}
import play.api.libs.json.Json

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class Collecting(ct: CollectTopic, ms: MonitorService)(implicit ec: ExecutionContext, mat: Materializer) {

  private implicit final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private final val source = ct.CollectTopicSSH.subscriber

  source.map(flowLog("debug", "receive ssh collect", _))
    .mapAsync(10)(x => {
      logger.info("start ssh collect")
      val c = collectAction(x.host, x.user, genUrl(x.metricName), Some(x.port)).map(rs => (x.id, rs))
      c.onComplete {
        case Success(a) => logger.info(a.toString())
        case Failure(ex) => logger.error(ex.getMessage)
      }
      c
    }).withAttributes(ActorAttributes.supervisionStrategy(decider))
    .mapAsync(10) { case (id, rsOpt) => ms.pushCollectResult.invoke(CollectResult(id, rsOpt)).map(_ => id) }.withAttributes(ActorAttributes.supervisionStrategy(decider))
    .runWith(Sink.foreach(id => logger.info(s"id:${id}, collect success.")))

  def flowLog[T](level: String, log: String, elem: T): T = {
    logger.info(log)
    elem
  }

  def decider(implicit log: Logger): Supervision.Decider = {
    case ex: Exception =>
      log.error(ex.getMessage + " XXXX")
      Supervision.Resume
  }

  def genUrl(name: String): String = {
    val formatUrl = ConfigFactory.load().getString("format.url")
    List(formatUrl, name, "ssh", "collect.sh").mkString("/")
  }

  def collectAction(host: String, user: String, scriptUrl: String, port: Option[Int] = Some(22)): Future[Option[String]] = Future {
    val OSName = System.getProperty("os.name").toLowerCase();
    try {
      if (OSName.startsWith("win")) {
        Some(ssh("C:\\Users\\zhangxu\\.ssh\\id_rsa", user, host, scriptUrl, port.get))
      } else if (OSName == "linux") {
        Some(ssh("~/.ssh/id_rsa", user, host, scriptUrl, port.get))
      } else {
        logger.error("OS not match..");
        None
      }
    } catch {
      case ex: Exception => logger.error(ex.getMessage + " collectionAction"); None
    }
  }

  def ssh(keyPath: String, user: String, host: String, scriptUrl: String, port: Int): String = {
    logger.info("ssh 1")
    val jsch = new JSch();
    jsch.addIdentity(keyPath);
    val session = jsch.getSession(user, host, port);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();
    val channelExec: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    val in = channelExec.getInputStream();
    channelExec.setCommand(s"curl -s ${scriptUrl} | sh");
    channelExec.connect();
    logger.info("ssh 2")
    val reader = new BufferedReader(new InputStreamReader(in));

    val rs = new ArrayBuffer[String]()

    var line: Option[String] = Option(reader.readLine())

    while (line.isDefined) {
      rs += line.get
      line = Option(reader.readLine())
    }
    logger.info("ssh 3")
    val exitStatus: Int = channelExec.getExitStatus();

    channelExec.disconnect();
    session.disconnect();
    logger.info("ssh 4")
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
