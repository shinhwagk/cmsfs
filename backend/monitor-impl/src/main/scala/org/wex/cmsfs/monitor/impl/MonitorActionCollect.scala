package org.wex.cmsfs.monitor.impl

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.ConfigFactory
import org.shinhwagk.query.api.{QueryOSMessage, QueryOracleMessage, QueryService}
import org.wex.cmsfs.config.api.{ConfigService, MonitorDepository}
import org.wex.cmsfs.monitor.api.{MonitorActionDepository, MonitorActionForJDBC, MonitorActionForSSH}

import scala.concurrent.{ExecutionContext, Future}

case class QueryResult(monitorId: Long, metricName: String, mode: String, collectData: String)

class MonitorActionCollect(mt: MonitorTopic,
                           cs: ConfigService,
                           qs: QueryService)(implicit ec: ExecutionContext, mi: Materializer) {

  mt.sshTopic.subscriber.mapAsync(10)(queryForSSH).mapAsync(10)(addMonitorDepository).runWith(Sink.foreach(mt.monitorDepositoryTopic.publish _))
  mt.jdbcTopic.subscriber.mapAsync(10)(queryForJDBC).mapAsync(10)(addMonitorDepository).runWith(Sink.foreach(mt.monitorDepositoryTopic.publish _))

  def queryForSSH(m: MonitorActionForSSH): Future[QueryResult] = {
    qs.queryForOSScript.invoke(QueryOSMessage(m.ip, m.user, genUrl("COLLECT", "SSH", m.metricName), Some(m.port)))
      .map(QueryResult(m.id, m.metricName, "SSH", _))
  }

  def queryForJDBC(m: MonitorActionForJDBC): Future[QueryResult] = {
    qs.queryForOracle("ARRAY").invoke(QueryOracleMessage(m.url, m.user, m.password, "select * from dual", List()))
      .map(QueryResult(m.id, m.metricName, "JDBC", _))
  }

  def addMonitorDepository(qr: QueryResult) = {
    val monitorDepository = MonitorDepository(None, qr.monitorId, qr.collectData)
    cs.addMonitorDepository
      .invoke(monitorDepository)
      .map(optionId => MonitorActionDepository(optionId, qr.monitorId, qr.metricName, qr.collectData))
  }

  def genUrl(stage: String, mode: String, name: String): String = {
    val formatUrl = ConfigFactory.load().getString("format.url")
    stage match {
      case "COLLECT" => List(formatUrl, name, mode.toLowerCase, "collect.sh").mkString("/")
      case "ANALYZE" => List(formatUrl, name, "analyze.py").mkString("/")
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
