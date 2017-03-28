package org.wex.cmsfs.bootstrap

import java.util.Date

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import org.quartz.CronExpression
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.jdbc.api.CollectJDBCService
import org.wex.cmsfs.collect.ssh.api.CollectSSHService
import org.wex.cmsfs.common.`object`
import org.wex.cmsfs.common.`object`.{CoreMonitorDetailForJdbc, CoreMonitorDetailForSsh}
import org.wex.cmsfs.config.api.{CoreFormatAnalyze, _}

import scala.concurrent.{ExecutionContextExecutor, Future, Promise}
import scala.util.{Failure, Success}

class MonitorActionCollect(cs: ConfigService,
                           cSSHs: CollectSSHService,
                           cJDBCs: CollectJDBCService,
                           system: ActorSystem)(implicit mat: Materializer) {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  Future {
    while (true) {
      logger.info(s"${System.currentTimeMillis()}")
      schedulerMonitor
      Thread.sleep(1000)
    }
  }

  def schedulerMonitor = {
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
    val coreCollectFuture: Future[CoreCollect] = cs.getCoreCollectById(cmd.collectId).invoke()
    val formatAnalyzeFuture = cs.getCoreFormatAnalyzesById(cmd.formatAnalyzeId.get).invoke()
    val formatAlarmFuture = cs.getCoreFormatAlarmsById(cmd.formatAlarmId.get).invoke()

    val p: Promise[Done] = Promise[Done]
    val f: Future[Done] = p.future

    cmd.connectorMode.toUpperCase match {
      case "JDBC" =>
        p completeWith {
          for {
            collect <- coreCollectFuture
            connectorJdbc <- cs.getCoreConnectorJdbcById(cmd.connectorId).invoke()
            formatAnalyze <- formatAnalyzeFuture
            formatAlarm <- formatAlarmFuture
            done <- sendMonitorForJdbc(cmd.id.get, utcDate, collect, connectorJdbc, formatAnalyze, formatAlarm)
          } yield done
        }
      case "SSH" =>
        p completeWith {
          for {
            collect <- coreCollectFuture
            connectorSsh <- cs.getCoreConnectorSshById(cmd.connectorId).invoke()
            formatAnalyze <- formatAnalyzeFuture
            formatAlarm <- formatAlarmFuture
            done <- sendMonitorForSsh(cmd.id.get, utcDate, collect, connectorSsh, formatAnalyze, formatAlarm)
          } yield done
        }
    }

    f.onComplete {
      case Success(_) => logger.info("send collect " + cmd.collectId.toString)
      case Failure(ex) => logger.error("send collect " + ex.getMessage)
    }
  }

  def sendMonitorForJdbc(id: Int, utcDate: String, collect: CoreCollect, connector: CoreConnectorJdbc, analyze: CoreFormatAnalyze, alarm: CoreFormatAlarm): Future[Done] = {
    val ccj = `object`.CoreConnectorJdbc(connector.id.get, connector.name, connector.url, connector.user, connector.password)
    val cc = `object`.CoreCollect(collect.id.get, collect.name, collect.path, collect.args)

    val es = `object`.CoreElasticsearch(analyze._index, analyze._type)
    val formatAnalyze = `object`.CoreFormatAnalyze(analyze.id.get, analyze.path, analyze.args, es)

    val formatAlarm = `object`.CoreFormatAlarm(alarm.id.get, alarm.path, alarm.args)

    cJDBCs.pushCollectItem2.invoke(CoreMonitorDetailForJdbc(id, utcDate, ccj, cc, Some(formatAnalyze), Some(formatAlarm)))
  }

  def sendMonitorForSsh(id: Int, utcDate: String, collect: CoreCollect, connector: CoreConnectorSsh, analyze: CoreFormatAnalyze, alarm: CoreFormatAlarm): Future[Done] = {
    val ccj = `object`.CoreConnectorSsh(connector.id.get, connector.name, connector.ip, connector.user, connector.password, connector.privateKey)
    val cc = `object`.CoreCollect(collect.id.get, collect.name, collect.path, collect.args)

    val es = `object`.CoreElasticsearch(analyze._index, analyze._type)
    val formatAnalyze = `object`.CoreFormatAnalyze(analyze.id.get, analyze.path, analyze.args, es)

    val formatAlarm = `object`.CoreFormatAlarm(alarm.id.get, alarm.path, alarm.args)

    cSSHs.pushCollectItem2.invoke(CoreMonitorDetailForSsh(id, utcDate, ccj, cc, Some(formatAnalyze), Some(formatAlarm)))
  }
}