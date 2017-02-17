package org.wex.cmsfs.format.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait FormatService extends Service {

  def pushFormatAnalyze(metricName: String, collectData: String): ServiceCall[NotUsed, Done]

  def pushFormatAlarm(monitorId: Int, collectId: Long): ServiceCall[FormatItem, Done]

  override final def descriptor = {
    import Service._
    named("format").withCalls(
      pathCall("/v1/format/analyze/:metricName/:collectData", pushFormatAnalyze _),
      pathCall("/v1/format/alarm/:mid/:cid", pushFormatAlarm _)
    )
  }
}