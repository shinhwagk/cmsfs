package org.wex.cmsfs.collect.jdbc.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import org.wex.cmsfs.common.`object`.CoreMonitorDetailForJdbc

object CollectJDBCService {
  val SERVICE_NAME = "collect-jdbc"
}

trait CollectJDBCService extends Service {

  def pushCollectItem: ServiceCall[CollectItemJdbc, Done]

  def pushCollectItem2: ServiceCall[CoreMonitorDetailForJdbc, Done]

  override final def descriptor = {
    import Service._
    named(CollectJDBCService.SERVICE_NAME).withCalls(
      restCall(Method.POST, "/v1/collect", pushCollectItem),
      restCall(Method.POST, "/v1/collect", pushCollectItem2)

    )
  }
}