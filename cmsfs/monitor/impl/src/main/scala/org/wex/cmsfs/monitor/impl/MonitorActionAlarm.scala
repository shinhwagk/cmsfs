package org.wex.cmsfs.monitor.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import org.wex.cmsfs.common.core.CmsfsAkkaStream
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
}