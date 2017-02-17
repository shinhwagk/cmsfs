package org.wex.cmsfs.monitor.impl

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import org.wex.cmsfs.format.api.FormatService

import scala.concurrent.ExecutionContext

class MonitorActionAnalyze(mt: MonitorTopic, fs: FormatService)(implicit ec: ExecutionContext, mi: Materializer) {

  private val log = LoggerFactory.getLogger(classOf[MonitorActionAnalyze])

  mt.monitorDepositoryTopic
    .subscriber
    .map { p => log.info("fasong  anglay "); p }
    .mapAsync(10)(md => fs.pushFormatAnalyze(md.metricName, md.collectData).invoke())
    .runWith(Sink.ignore)

  //  def executeMonitorForAnalyze(md: MonitorActionDepository): Future[String] = {
  //    for {
  //
  //      fs.pushFormatAnalyze()
  //      metric <- cs.getMetricById(md.monitorId).invoke()
  //      c <- cs.getConnectorSSHById(md.ConnectorId).invoke()
  //      mh <- cs.getMachineById(c.machineId).invoke()
  //      collectData <- qs.queryForOSScript
  //        .invoke(QueryOSMessage(mh.ip, c.user, genUrl("COLLECT", "SSH", metric.name), Some(c.port)))
  //    } yield collectData
  //  }
}
