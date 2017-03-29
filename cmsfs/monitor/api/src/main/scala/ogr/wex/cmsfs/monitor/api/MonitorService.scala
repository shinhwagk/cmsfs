package ogr.wex.cmsfs.monitor.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

object MonitorService {
  val SERVICE_NAME = "monitor"
}

trait MonitorService extends Service {

  def pushCollectItem: ServiceCall[NotUsed, Done]

  override final def descriptor = {
    import Service._
    import MonitorService._
    named(SERVICE_NAME).withCalls(
      restCall(Method.POST, "/v1/collect", pushCollectItem)
    )
  }
}