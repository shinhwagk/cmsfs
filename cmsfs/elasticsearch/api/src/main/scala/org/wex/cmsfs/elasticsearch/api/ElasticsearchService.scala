package org.wex.cmsfs.elasticsearch.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait ElasticsearchService extends Service{
  def pushFormatAnalyze: ServiceCall[FormatAnalyzeItem, Done]

  override final def descriptor = {
    import FormatAnalyzeService._
    import Service._
    named(SERVICE_NAME).withCalls(
      pathCall("/v1/format/analyze", pushFormatAnalyze)
    )
  }
}
