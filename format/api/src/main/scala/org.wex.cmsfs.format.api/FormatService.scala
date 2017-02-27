package org.wex.cmsfs.format.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import org.wex.cmsfs.format.api.format.{AlarmItem, AnalyzeItem}

trait FormatService extends Service {

  def pushFormatAnalyze: ServiceCall[AnalyzeItem, Done]

  def pushFormatAlarm: ServiceCall[AlarmItem, Done]

  override final def descriptor = {
    import Service._
    named("format").withCalls(
      pathCall("/v1/format/analyze", pushFormatAnalyze),
      pathCall("/v1/format/alarm/:mid/:cid", pushFormatAlarm)
    )
  }
}