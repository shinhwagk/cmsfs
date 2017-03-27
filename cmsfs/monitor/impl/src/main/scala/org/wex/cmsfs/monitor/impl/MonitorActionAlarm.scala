package org.wex.cmsfs.monitor.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import org.wex.cmsfs.common.CmsfsAkkaStream
import org.wex.cmsfs.config.api.{ConfigService, CoreMonitorDetail}
import org.wex.cmsfs.format.alarm.api.{FormatAlarmItem, FormatAlarmService}
import scala.concurrent.Future

class MonitorActionAlarm(mt: MonitorTopic,
                         fas: FormatAlarmService,
                         cs: ConfigService,
                         system: ActorSystem)(implicit mi: Materializer) extends CmsfsAkkaStream {

  implicit val logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  logger.info(s"${this.getClass} start.")

  /**
    * alarm format
    */
//  mt.collectResultTopic.subscriber
//    .map { elem => loggerFlow(elem, "alarm action " + elem.monitorDetailId) }
//    .filter(_.rs.isDefined)
//    //    .mapAsync(10)(x => fas.pushFormatAnalyze.invoke(FormatAnalyzeItem(x.id, x.metricName, x.rs.get, "[]", x.utcDate, x.name)))
//    .runWith(Sink.ignore)
  mt.collectResultTopic.subscriber
    .map { elem => loggerFlow(elem, "alarm action " + elem.monitorDetailId) }
    .mapAsync(10) { i =>
      val monitorDetailId = i.monitorDetailId
      val coreMonitorDetailFuture: Future[CoreMonitorDetail] = cs.getCoreMonitorDetailById(monitorDetailId).invoke()
      for {
        coreMonitorDetail <- coreMonitorDetailFuture if coreMonitorDetail.formatAlarmId.isDefined
        coreFormatAlarm <- cs.getCoreFormatAnalyzesById(coreMonitorDetail.formatAlarmId.get).invoke()
        done <- {
          val p = coreFormatAlarm
          val formatAlarmItem =
            FormatAlarmItem(i.connectorName, p._index, p._metric, i.utcDate, i.rs.get, p.path, coreMonitorDetail.formatAlarmArgs.getOrElse("[]"))
          fas.pushFormatAlarm.invoke(formatAlarmItem)
        }
      } yield done
    }.withAttributes(supervisionStrategy((x) => x + "xx"))
    .runWith(Sink.ignore)
  //    .subscriber
  //    .map { p => log.info("fasong  anglay "); p }
  //    .mapAsync(10)(md => fs.pushFormatAnalyze(md.metricName, md.collectData).invoke())
  //    .runWith(Sink.ignore)

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