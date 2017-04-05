package ogr.wex.cmsfs.monitor.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import org.wex.cmsfs.config.api.CoreMonitorStatus

object MonitorService {
  val SERVICE_NAME = "monitor"
}

trait MonitorService extends Service {

  def getMonitorStatus: ServiceCall[NotUsed, Seq[CoreMonitorStatus]]

  override final def descriptor = {
    import Service._
    import MonitorService._
    named(SERVICE_NAME).withCalls(
      restCall(Method.GET, "/v1/monitor/statuses", getMonitorStatus)
    )
  }
}