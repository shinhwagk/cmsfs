package org.wex.cmsfs.monitor.impl

import java.util.Date

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.quartz.CronExpression
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.jdbc.api.{CollectItemJdbc, CollectJDBCService}
import org.wex.cmsfs.collect.ssh.api.CollectSSHService
import org.wex.cmsfs.config.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}

class MonitorActionCollect(mt: MonitorTopic,
                           cs: ConfigService,
                           cSSHs: CollectSSHService,
                           cJDBCs: CollectJDBCService,
                           system: ActorSystem)(implicit mat: Materializer) {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  Future {
    while (true) {
      logger.info(s"${System.currentTimeMillis()}")
      val cDate = new Date()
      val utcDate = cDate.toInstant.toString
      val monitorDetails: Future[Seq[CoreMonitorDetail]] = cs.getCoreMonitorDetails.invoke().map(_.filter(cmd => filterCron(cmd.cron, cDate)))
      val dispatcher = monitorDistributor(utcDate)(_)
      monitorDetails.foreach(_.foreach(dispatcher))
      Thread.sleep(1000)
    }
  }

  def filterCron(cron: String, cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }

  def monitorDistributor(utcDate: String)(cmd: CoreMonitorDetail): Unit = {
    val coreCollect: Future[CoreCollect] = cs.getCoreCollectById(cmd.collectId).invoke()
    cmd.category.toUpperCase match {
      case "RDB" =>
        val coreConnector: Future[CoreConnectorJdbc] = cs.getCoreConnectorJdbcById(cmd.connectorId).invoke()
        Future.sequence(List(coreCollect, coreConnector)).onComplete {
          case Success((collect, connector)) => cJDBCs.pushCollectItem.invoke(CollectItemJdbc(cmd.id.get, collect, connector, utcDate))
          case Failure(ex) => logger.error(ex.getMessage)
        }
      case "SSH" =>
        val coreConnector: Future[CoreConnectorSsh] = cs.getCoreConnectorSshById(cmd.connectorId).invoke()
        Future.sequence(List(coreCollect, coreConnector)).onComplete {
          case Success((collect, connector)) => cSSHs.pushCollectItem.invoke(CollectItemSsh(cmd.id.get, collect, connector, utcDate))
          case Failure(ex) => logger.error(ex.getMessage)
        }
    }
  }
}