package org.wex.cmsfs.notification.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

object NotificationService {
  val SERVICE_NAME = "notification"
}

trait NotificationService extends Service {

  def pushNotificationItem: ServiceCall[NotificationItem, Done]

  override final def descriptor = {
    import NotificationService._
    import Service._
    named(SERVICE_NAME).withCalls(
      restCall(Method.POST, "/v1/notification/email", pushNotificationItem),
      restCall(Method.POST, "/v1/notification/phone", pushNotificationItem)
    )
  }
}
