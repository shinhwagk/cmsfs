package org.wex.cmsfs.monitor.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait MonitorService extends Service {

  def pushMonitorItem: ServiceCall[MonitorItem, Done]

  override final def descriptor = {
    import Service._
    named("monitor").withCalls(
      restCall(Method.POST, "/v1/monitor", pushMonitorItem)
    )
  }
}