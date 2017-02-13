package org.cmsfs.collecting.api

import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait CollectingService extends Service {

  def createCollectItem: ServiceCall[CollectItem, CollectItem]

  override final def descriptor = {
    import Service._
    named("collecting").withCalls(
      pathCall("/v1/collecting", createCollectItem)
    )
  }
}
