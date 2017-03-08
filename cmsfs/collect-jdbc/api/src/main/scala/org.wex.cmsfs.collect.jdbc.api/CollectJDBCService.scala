package org.wex.cmsfs.collect.jdbc.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

object CollectJDBCService {
  val SERVICE_NAME = "collect-jdbc"
}

trait CollectJDBCService extends Service {

  def pushCollectItem: ServiceCall[CollectItemJDBC, Done]

  override final def descriptor = {
    import Service._
    named(CollectJDBCService.SERVICE_NAME).withCalls(
      restCall(Method.POST, "/v1/collect", pushCollectItem)
    )
  }
}