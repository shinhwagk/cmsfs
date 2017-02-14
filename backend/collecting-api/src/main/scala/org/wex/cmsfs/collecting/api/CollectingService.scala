package org.wex.cmsfs.collecting.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait CollectingService extends Service {

  def pushCollectItem: ServiceCall[CollectItem, Done]

  override final def descriptor = {
    import Service._
    named("collecting").withCalls(
      pathCall("/v1/collecting", pushCollectItem)
    )
  }
}
