package org.wex.cmsfs.collect.ssh.impl

import java.io.{BufferedReader, InputStreamReader}

import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.jcraft.jsch.{ChannelExec, JSch}
import org.slf4j.Logger
import org.wex.cmsfs.common.`object`.{CoreCollect, CoreConnectorSsh, CoreMonitorDetailForSsh}
import org.wex.cmsfs.common.collect.CollectCore
import org.wex.cmsfs.common.core.{CmsfsAkkaStream, Common}
import org.wex.cmsfs.format.alarm.api.{FormatAlarmItem, FormatAlarmService}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem, FormatAnalyzeService}
import play.api.libs.json.Json

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContextExecutor, Future}

abstract class CollectSsh(analyzeService: FormatAnalyzeService,
                          alarmService: FormatAlarmService)
  extends CollectCore with CmsfsAkkaStream with Common {

  val logger: Logger

  implicit val executionContext: ExecutionContextExecutor

  val flow: Flow[CoreMonitorDetailForSsh, Int, NotUsed] = Flow[CoreMonitorDetailForSsh]
    .map(elem => loggerFlow(elem, s"receive ssh collect ${elem.connector.id}"))
    .mapAsync(10) { cmdfs =>

      logger.debug(s"collect ssh elem: ${Json.toJson(cmdfs.id).toString()}")

      val collectTimeCalculateFun: (String) => String = collectTimeMonitor

      collectFun(cmdfs.connector, cmdfs.collect)
        .map(elem => loggerFlow(elem, collectTimeCalculateFun(cmdfs.collect.name + " " + cmdfs.connector.name)))
        .filter(_.isDefined)
        .map(_.get)
        .flatMap { collectResult =>
          val a = cmdfs.analyze match {
            case Some(cfa) => analyzeService.pushFormatAnalyze.invoke(FormatAnalyzeItem(cmdfs.id, cmdfs.collect.name, cmdfs.utcDate, collectResult, cfa))
            case None => Future.successful(Done)
          }
          val b = cmdfs.alarm match {
            case Some(cfa) => alarmService.pushFormatAlarm.invoke(FormatAlarmItem(cmdfs.id, collectResult, cfa))
            case None => Future.successful(Done)
          }
          Future.sequence(a :: b :: Nil).map(_ => cmdfs.id)
        }
    }

  def collectFun(cr: CoreConnectorSsh, ct: CoreCollect): Future[Option[String]] = {
    collectAction(cr.ip, cr.user, getUrlByPath(ct.path))
  }

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
