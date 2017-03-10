package org.wex.cmsfs.monitor.impl

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import org.wex.cmsfs.format.alarm.api.FormatAlarmService

import scala.concurrent.ExecutionContext

class MonitorActionAlarm(mt: MonitorTopic, fas: FormatAlarmService)(implicit ec: ExecutionContext, mi: Materializer) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  mt.collectResultTopic.subscriber
    .filter(_.rs.isDefined)
        .mapAsync(10)(x => fas.pushFormatAnalyze.invoke(FormatAnalyzeItem(x.id, "xx", x.rs.get, "xx")))
    .runWith(Sink.ignore)
}