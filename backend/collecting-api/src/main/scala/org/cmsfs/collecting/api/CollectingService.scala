package org.cmsfs.collecting.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait CollectingService extends Service {

  def createCollectItem: ServiceCall[CollectItem, CollectItem]

  def pushCollectItem: ServiceCall[CollectItem, Done]

  override final def descriptor = {
    import Service._
    named("collecting").withCalls(
      pathCall("/v1/collecting", createCollectItem)
    )
  }
}
