package org.wex.cmsfs.monitor.status.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

object MonitorStatusService {
  val SERVICE_NAME = "monitor-status"
}

trait MonitorStatusService extends Service {

  def putCoreMonitorStatus(id: Int, category: String, name: String, metric: String): ServiceCall[NotUsed, Done]

  def putCoreMonitorStatusCollect(id: Int): ServiceCall[CoreMonitorStatusCollect, Done]

  def putCoreMonitorStatusAnalyze(id: Int): ServiceCall[CoreMonitorStatusAnalyze, Done]

  def putCoreMonitorStatusAlarm(id: Int): ServiceCall[CoreMonitorStatusAlarm, Done]

  def getCoreMonitorStatuses: ServiceCall[NotUsed, Seq[CoreMonitorStatus]]

  override final def descriptor = {
    import Service._
    import MonitorStatusService._
    named(SERVICE_NAME).withCalls(
      restCall(Method.PUT, "/v1/core/monitor/status/:id/:category/:name/:metric", putCoreMonitorStatus _),
      restCall(Method.PUT, "/v1/core/monitor/status/collect/:id", putCoreMonitorStatusCollect _),
      restCall(Method.PUT, "/v1/core/monitor/status/analyze/:id", putCoreMonitorStatusAnalyze _),
      restCall(Method.PUT, "/v1/core/monitor/status/alarm/:id", putCoreMonitorStatusAlarm _),
      restCall(Method.GET, "/v1/core/monitor/statuses", getCoreMonitorStatuses)
    )
  }
}
