package org.wex.cmsfs.format.alarm.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

object FormatAlarmService {
  val SERVICE_NAME = "format-alarm"
}

trait FormatAlarmService extends Service {

  def pushFormatAlarm: ServiceCall[FormatAlarmItem, Done]

  override final def descriptor = {
    import FormatAlarmService._
    import Service._
    named(SERVICE_NAME).withCalls(
      pathCall("/v1/format/alarm", pushFormatAlarm)
    )
  }
}
