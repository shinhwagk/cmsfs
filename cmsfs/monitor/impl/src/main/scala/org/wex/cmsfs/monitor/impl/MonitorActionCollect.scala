package org.wex.cmsfs.monitor.impl

import java.util.Date

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import org.quartz.CronExpression
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.jdbc.api.CollectJDBCService
import org.wex.cmsfs.collect.ssh.api.CollectSSHService
import org.wex.cmsfs.common.`object`
import org.wex.cmsfs.common.`object`.CoreMonitorDetailForJdbc
import org.wex.cmsfs.config.api.{CoreFormatAnalyze, _}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContextExecutor, Future, Promise}
import scala.util.{Failure, Success}

class MonitorActionCollect(cs: ConfigService,
                           cSSHs: CollectSSHService,
                           cJDBCs: CollectJDBCService,
                           system: ActorSystem)(implicit mat: Materializer) {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  logger.info(s"${this.getClass.getName} start.")

  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  Future {
    while (true) {
      logger.info(s"${System.currentTimeMillis()}");
      schedulerMonitor;
      Thread.sleep(1000);
    }
  }

  def schedulerMonitor: Unit = {
    val cDate = new Date()
    val filterMonitorDetailsFunByCron = filterMonitorDetails(cDate)(_)

    val utcDate = cDate.toInstant.toString
    val dispatcherFun = monitorDistributor(utcDate)(_)

    cs.getCoreMonitorDetails.invoke().map(filterMonitorDetailsFunByCron) onComplete {
      case Success(monitorDetails) => monitorDetails.foreach(dispatcherFun)
      case Failure(ex) => logger.error(s"schedulerMonitor ${ex.getMessage}")
    }
  }

  def filterMonitorDetails(cDate: Date)(monitorDetails: Seq[CoreMonitorDetail]): Seq[CoreMonitorDetail] = {
    monitorDetails.filter(cmd => filterCron(cmd.cron, cDate))
  }

  def filterCron(cron: String, cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }

  def monitorDistributor(utcDate: String)(cmd: CoreMonitorDetail): Unit = {
    logger.info(s"monitor details: ${cmd.formatAlarmIds.toString}")

    val coreCollectFuture: Future[CoreCollect] = cs.getCoreCollectById(cmd.collectId).invoke()

    val formatAnalyzeFuture = cmd.formatAnalyzeId match {
      case Some(id) => cs.getCoreFormatAnalyzesById(id).invoke().map(Some(_))
      case None => Future.successful(None)
    }

    val formatAlarmsFuture: Future[Seq[`object`.CoreFormatAlarm]] = {
      cmd.formatAlarmIds.map(id => cs.getCoreFormatAlarmsById(id).invoke())


      val c = Future.sequence(cmd.formatAlarmIds.map(id => cs.getCoreFormatAlarmsById(id).invoke()))
        .map(_.map(apiAlarm =>
          `object`.CoreFormatAlarm(apiAlarm.id.get, apiAlarm.path, apiAlarm.args, `object`.CoreFormatAlarmNotification(apiAlarm.notification.mails, apiAlarm.notification.mobiles))))

      logger.info("aaa: " + cmd.formatAlarmIds)
      c.foreach(p => logger.info("dddd:  " + p.toString))

      c
    }

    formatAlarmsFuture.foreach(p => logger.info(s"future ${cmd.formatAlarmIds.toString()}, ${p.toString()}"))

    val p: Promise[Seq[Done]] = Promise[Seq[Done]]
    val f: Future[Seq[Done]] = p.future

    cmd.connectorMode.toUpperCase match {
      case "JDBC" =>
        p completeWith {
          for {
            collect <- coreCollectFuture
            connectors <- Future.sequence(cmd.connectorIds.map(id => cs.getCoreConnectorJdbcById(id).invoke()))
            formatAnalyze <- formatAnalyzeFuture
            formatAlarms <- formatAlarmsFuture
            dones <- sendMonitorForJdbc(cmd.id.get, utcDate, collect, connectors, formatAnalyze, formatAlarms)
          } yield dones
        }
      case "SSH" =>
        p completeWith {
          for {
            collect <- coreCollectFuture
            connectors <- Future.sequence(cmd.connectorIds.map(id => cs.getCoreConnectorSshById(id).invoke()))
            formatAnalyze <- formatAnalyzeFuture
            formatAlarms <- formatAlarmsFuture
            dones <- sendMonitorForSsh(cmd.id.get, utcDate, collect, connectors, formatAnalyze, formatAlarms)
          } yield dones
        }
    }

    f.onComplete {
      case Success(_) => logger.info("send collect " + cmd.collectId.toString)
      case Failure(ex) => logger.error("send collect " + ex.getMessage)
    }
  }

  def sendMonitorForJdbc(id: Int, utcDate: String,
                         collect: CoreCollect,
                         connectors: Seq[CoreConnectorJdbc],
                         analyze: Option[CoreFormatAnalyze],
                         alarms: Seq[`object`.CoreFormatAlarm]): Future[Seq[Done]] = {
    val cc = `object`.CoreCollect(collect.id.get, collect.name, collect.path, collect.args)

    val ccjs = connectors.map(connector => `object`.CoreConnectorJdbc(connector.id.get, connector.name, connector.url, connector.user, connector.password))

    logger.info(s"formatAlarms: ${alarms.toString()}")

    val dones = ccjs.map { ccj =>
      val sendFormatAnalyzeOpt = analyze match {
        case Some(a) =>
          Some(`object`.CoreFormatAnalyze(a.id.get, a.path, a.args, `object`.CoreElasticsearch(a._index, ccj.name)))
        case None => None
      }

      cJDBCs.pushCollectItem.invoke(CoreMonitorDetailForJdbc(id, utcDate, ccj, cc, sendFormatAnalyzeOpt, alarms))
    }

    Future.sequence(dones)
  }

  def sendMonitorForSsh(id: Int, utcDate: String,
                        collect: CoreCollect,
                        connectors: Seq[CoreConnectorSsh],
                        analyze: Option[CoreFormatAnalyze],
                        alarms: Seq[`object`.CoreFormatAlarm]): Future[Seq[Done]] = {
    val ccjs = connectors.map(connector => `object`.CoreConnectorSsh(connector.id.get, connector.name, connector.ip, connector.user, connector.password, connector.privateKey))

    val cc = `object`.CoreCollect(collect.id.get, collect.name, collect.path, collect.args)

    logger.info(s"formatAlarms: ${alarms.toString()}")

    val dones = ccjs.map { ccj =>
      val formatAnalyzeOpt = analyze match {
        case Some(a) =>
          Some(`object`.CoreFormatAnalyze(a.id.get, a.path, a.args, `object`.CoreElasticsearch(a._index, ccj.name)))
        case None => None
      }

      cSSHs.pushCollectItem.invoke(`object`.CoreMonitorDetailForSsh(id, utcDate, ccj, cc, formatAnalyzeOpt, alarms))
    }

    Future.sequence(dones)
  }
}