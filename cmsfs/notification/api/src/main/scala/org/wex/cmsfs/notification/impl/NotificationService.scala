package org.wex.cmsfs.notification.impl

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
      pathCall("/v1/notification", pushNotificationItem)
    )
  }
}
