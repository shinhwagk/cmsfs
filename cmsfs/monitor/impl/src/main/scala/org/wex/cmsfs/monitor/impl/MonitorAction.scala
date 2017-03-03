package org.wex.cmsfs.monitor.impl

import java.util.Date

import akka.stream.Materializer
import org.quartz.CronExpression
import org.shinhwagk.query.api.QueryService
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.config.api._
import org.wex.cmsfs.format.api.FormatService
import org.wex.cmsfs.monitor.api._

import scala.concurrent.{ExecutionContext, Future}

class MonitorAction(mt: MonitorTopic,
                    cs: ConfigService,
                    qs: QueryService,
                    fs: FormatService)(implicit ec: ExecutionContext, mi: Materializer) {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  Future {
    while (true) {
      logger.info(s"${System.currentTimeMillis()}")
      val cDate = new Date()
      val monitorDetails: Future[Seq[MonitorDetail]] = cs.getMonitorDetails.invoke().map(_.filter(md => filterCron(md.cron, cDate)))
      monitorDetails.foreach(_.foreach(monitorCategory))
      Thread.sleep(1000)
    }
  }

  def filterCron(cron: String, cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }

  def monitorCategory(md: MonitorDetail): Unit = {
    cs.getMetricById(md.metricId).invoke().foreach(mc =>
      mc.mode match {
        case "JDBC" =>
          cs.getConnectorJDBCById(md.ConnectorId).invoke().foreach { case ConnectorModeJDBC(_, _, _, _, url, user, password, _, _, _) =>
            logger.info(s"debug: push jdbc collect ${md.id}")
            mt.jdbcCollectTopic.publish(MonitorActionForJDBC(md.id, mc.name, md.collectArgs, md.analyzeArgs, url, user, password))
          }
        case "SSH" =>
          cs.getConnectorSSHById(md.ConnectorId).invoke().foreach { case ConnectorModeSSH(_, mId, _, _, port, user, password, privateKey, _, _, _) =>
            cs.getMachineById(mId).invoke().foreach(m => {
              logger.info(s"debug: push ssh collect ${md.id}")
              mt.sshCollectTopic.publish(MonitorActionForSSH(md.id, mc.name, md.collectArgs, md.analyzeArgs, m.ip, port, user, password, privateKey))
            })
          }
      })
  }

  //
  //  val monitorDepositoryTopic = pubSub.refFor(TopicId[MonitorDepository])
  //
  ////  MonitorActionAnalyze.start(monitorDepositoryTopic)
  //
  //  val jdbcSub = jdbcTopic.subscriber
  //  //  val sshSub =
  //
  //  loopCollecting
  //
  //  def loopCollecting = Future {
  //    while (true) {
  //      println(System.currentTimeMillis())
  //      val cDate = new Date() // current Date
  //      cs.getMonitorDetails
  //        .invoke()
  //        .foreach(_.filter(cd => filterCron(cd.cron, cDate))
  //          .foreach { cd =>
  //            println(cd)
  //            cs.getMetricById(cd.metricId)
  //              .invoke()
  //              .foreach(_.mode match {
  //                case "JDBC" => jdbcTopic.publish(cd)
  //                case _ => sshTopic.publish(cd)
  //              })
  //          })
  //      Thread.sleep(1000)
  //    }
  //  }
  //
  //  //  jdbcSub.mapAsync(1) { cd =>
  //  //    val cId = cd.ConnectorId
  //  //    val mId = cd.monitorId
  //  //    for {
  //  //      m <- cs.getMonitorJDBCbyId(mId).invoke()
  //  //      c <- cs.getConnectorJDBCById(cId).invoke()
  //  //      q <- qs.queryForOracle("ARRAY")
  //  //        .invoke(QueryOracleMessage(c.url, c.user, c.password, m.code, cd.args))
  //  //    } yield DepositoryCollect(None, cd.id, Json.toJson(c).toString(), Json.toJson(m).toString(), q)
  //  //  }.mapAsync(1)(cs.addDepositoryCollect.invoke _).runWith(Sink.ignore)
  //
  //  sshTopic.subscriber.mapAsync(10) { md =>
  //    val a = Random.nextInt()
  //    println("收到ssh", a)
  //    try {
  //      executeMonitorForSSH(md)
  //        .map(c => Some(MonitorDepository(None, md.id, c, None, None, System.currentTimeMillis())))
  //    } catch {
  //      case ex: Exception => {
  //        //        throw new Exception(ex.getMessage)
  //        Future.successful(None)
  //      }
  //    }
  //  }.filter(_.isDefined)
  //    .map(_.get)
  //    .mapAsync(10)(md => cs.addMonitorDepository.invoke(md))
  //    .runWith(Sink.foreach(monitorDepositoryTopic.publish _))
  //
  //
  //  def genUrl(stage: String, mode: String, name: String): String = {
  //    val formatUrl = ConfigFactory.load().getString("format.url")
  //
  //    stage match {
  //      case "COLLECT" => formatUrl + "/" + name + "/" + mode.toLowerCase + "/" + "collect.sh"
  //      case "ANALYZE" => formatUrl + "/" + name + "analyze.py"
  //    }
  //  }
  //
  //  //  def executeMonitor(md: MonitorDetail) = {
  //  //    for {
  //  //      metric <- cs.getMetricById(md.metricId).invoke()
  //  //      collectData <- monitorDistributor(metric, md)
  //  //    } yield collectData
  //  //
  //  //  }
  //  //
  //  //  def monitorDistributor(metric: Metric, md: MonitorDetail): Future[String] = {
  //  //    metric.mode match {
  //  //      case "SSH" =>
  //  //        executeMonitorForSSH(md, metric.name)
  //  //      case "JDBC" =>
  //  //        Future.successful("")
  //  //    }
  //  //  }
  //
  //  def executeMonitorForSSH(md: MonitorDetail): Future[String] = {
  //    for {
  //      metric <- cs.getMetricById(md.metricId).invoke()
  //      c <- cs.getConnectorSSHById(md.ConnectorId).invoke()
  //      mh <- cs.getMachineById(c.machineId).invoke()
  //      collectData <- qs.queryForOSScript
  //        .invoke(QueryOSMessage(mh.ip, c.user, genUrl("COLLECT", "SSH", metric.name), Some(c.port)))
  //    } yield collectData
  //  }

}