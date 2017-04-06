package org.wex.cmsfs.notification.impl

import com.lightbend.lagom.scaladsl.api.{CircuitBreaker, Service, ServiceCall}

object NotificationService {
  val SERVICE_NAME = "notification"
}

trait NotificationService extends Service {

  def pushNotificationItem: ServiceCall[String, String]

  override final def descriptor = {
    import NotificationService._
    import Service._
    named(SERVICE_NAME).withCalls(
      pathCall("/mns-web/services/rest/msgNotify", pushNotificationItem)
        .withCircuitBreaker(CircuitBreaker.identifiedBy("alarm-circuitbreaker"))
    )
  }
}
