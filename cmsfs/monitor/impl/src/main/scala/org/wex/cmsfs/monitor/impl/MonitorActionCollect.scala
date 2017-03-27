package org.wex.cmsfs.monitor.impl

import java.util.Date

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import org.quartz.CronExpression
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.jdbc.api.{CollectItemJdbc, CollectJDBCService}
import org.wex.cmsfs.collect.ssh.api.{CollectItemSsh, CollectSSHService}
import org.wex.cmsfs.config.api._
import org.wex.cmsfs.monitor.status.impl.MonitorStatusService

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class MonitorActionCollect(mt: MonitorTopic,
                           cs: ConfigService,
                           cSSHs: CollectSSHService,
                           cJDBCs: CollectJDBCService,
                           mss: MonitorStatusService,
                           system: ActorSystem)(implicit mat: Materializer) {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

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

    val p: Promise[(String, String)] = Promise[(String, String)]
    val f: Future[(String, String)] = p.future

    cmd.connectorMode.toUpperCase match {
      case "JDBC" =>
        p completeWith {
          for {
            coreCollect <- coreCollectFuture
            coreConnectorJdbc <- cs.getCoreConnectorJdbcById(cmd.connectorId).invoke()
            _ <- cJDBCs.pushCollectItem.invoke(CollectItemJdbc(cmd.id.get, coreCollect, coreConnectorJdbc, utcDate))
          } yield (coreConnectorJdbc.name, coreCollect.name)
        }
      case "SSH" =>
        p completeWith {
          for {
            coreCollect <- coreCollectFuture
            coreConnectorJdbc <- cs.getCoreConnectorSshById(cmd.connectorId).invoke()
            _ <- cSSHs.pushCollectItem.invoke(CollectItemSsh(cmd.id.get, coreCollect, coreConnectorJdbc, utcDate))
          } yield (coreConnectorJdbc.name, coreCollect.name)
        }
    }

    f.onComplete {
      case Success((name, metric)) => {
        val id = cmd.id.get
        val category = cmd.connectorMode
        mss.putCoreMonitorStatus(id, category, name, metric).invoke()
        logger.info("send collect " + cmd.collectId.toString)
      }
      case Failure(ex) => logger.error("send collect " + ex.getMessage)
    }
  }
}