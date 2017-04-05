package org.wex.cmsfs.notification.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.{CircuitBreaker, Service, ServiceCall}

object NotificationService {
  val SERVICE_NAME = "notification"
}

trait NotificationService extends Service {

  def pushNotificationItem: ServiceCall[NotificationItem, Done]

  override final def descriptor = {
    import NotificationService._
    import Service._
    named(SERVICE_NAME).withCalls(
      pathCall("/mns-web/services/rest/msgNotify", pushNotificationItem)
        .withCircuitBreaker(CircuitBreaker.identifiedBy("elasticsearch-circuitbreaker"))
    )
  }
}
