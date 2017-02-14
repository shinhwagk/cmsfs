package org.wex.cmsfs.format.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait FormatService extends Service {

  def pushFormat: ServiceCall[FormatItem, Done]

  override final def descriptor = {
    import Service._
    named("format").withCalls(
      pathCall("/v1/format", pushFormat)
    )
  }
}