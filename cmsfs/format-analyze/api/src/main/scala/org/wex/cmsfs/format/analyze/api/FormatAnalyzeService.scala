package org.wex.cmsfs.format.analyze.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

object FormatAnalyzeService {
  val SERVICE_NAME = "format-analyze"
}

trait FormatAnalyzeService extends Service {

  def pushFormatAnalyze: ServiceCall[FormatAnalyzeItem, Done]

  override final def descriptor = {
    import FormatAnalyzeService._
    import Service._
    named(SERVICE_NAME).withCalls(
      pathCall("/v1/format/analyze", pushFormatAnalyze)
    )
  }
}
