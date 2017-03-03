package org.wex.cmsfs.monitor.impl

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.ConfigFactory
import org.shinhwagk.query.api.{QueryOSMessage, QueryOracleMessage, QueryService}
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.config.api.{ConfigService, MonitorDepository}
import org.wex.cmsfs.format.api.FormatService
import org.wex.cmsfs.format.api.format.AnalyzeItem
import org.wex.cmsfs.monitor.api.{MonitorActionDepository, MonitorActionForJDBC, MonitorActionForSSH}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

case class QueryResult(monitorId: Long, metricName: String, mode: String, collectData: String)

class MonitorActionCollect(mt: MonitorTopic,
                           cs: ConfigService,
                           fs: FormatService,
                           qs: QueryService)(implicit ec: ExecutionContext, mi: Materializer) {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def flowLog[T](level: String, log: String, elem: T): T = {
    logger.info(log)
    elem
  }

  mt.sshCollectTopic.subscriber
    .map(flowLog("debug", "receive ssh collect", _))
    .mapAsync(10)(x=>{
      logger.info("start ssh collect")
      queryForSSH(x)
    })
    //    .mapAsync(10)(addMonitorDepository)
//    .mapAsync(10)(d => fs.pushFormatAnalyze.invoke(AnalyzeItem(d.monitorId, d.metricName, d.collectData, Nil)))
    .runWith(Sink.foreach(x => logger.info(s"${x}, test")))

  mt.jdbcCollectTopic.subscriber
    .mapAsync(10)(queryForJDBC)
    .map(flowLog("debug", "receive jdbc oracle collect", _))
    //    .mapAsync(10)(addMonitorDepository)
    .mapAsync(10)(d => fs.pushFormatAnalyze.invoke(AnalyzeItem(d.monitorId, d.metricName, d.collectData, Nil)))
    .runWith(Sink.ignore)

  def queryForSSH(m: MonitorActionForSSH): Future[QueryResult] = {
    qs.queryForOSScript.invoke(QueryOSMessage(m.ip, m.user, genUrl("SSH", m.metricName), Some(m.port)))
      .map(QueryResult(m.id, m.metricName, "SSH", _))
  }

  def queryForJDBC(m: MonitorActionForJDBC): Future[QueryResult] = {
    val sqlText = Source.fromURL(genUrl("JDBC", m.metricName)).mkString
    qs.queryForOracle("ARRAY").invoke(QueryOracleMessage(m.url, m.user, m.password, sqlText, List()))
      .map(QueryResult(m.id, m.metricName, "JDBC", _))
  }

  //  def addMonitorDepository(qr: QueryResult) = {
  //    val monitorDepository = MonitorDepository(None, qr.monitorId, qr.collectData)
  //    cs.addMonitorDepository
  //      .invoke(monitorDepository)
  //      .map(optionId => MonitorActionDepository(optionId, qr.monitorId, qr.metricName, qr.collectData))
  //  }

  def genUrl(mode: String, name: String): String = {
    val formatUrl = ConfigFactory.load().getString("format.url")
    mode match {
      case "SSH" => List(formatUrl, name, mode.toLowerCase, "collect.sh").mkString("/")
      case "JDBC" => List(formatUrl, name, mode.toLowerCase, "collect.sql").mkString("/")
    }
  }

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

  //  def executeMonitorForAnalyze(md: MonitorDepository): Future[String] = {
  //    for {
  //      metric <- cs.getMetricById(md.monitorId).invoke()
  //      c <- cs.getConnectorSSHById(md.ConnectorId).invoke()
  //      mh <- cs.getMachineById(c.machineId).invoke()
  //      collectData <- qs.queryForOSScript
  //        .invoke(QueryOSMessage(mh.ip, c.user, genUrl("COLLECT", "SSH", metric.name), Some(c.port)))
  //    } yield collectData
  //  }
}
