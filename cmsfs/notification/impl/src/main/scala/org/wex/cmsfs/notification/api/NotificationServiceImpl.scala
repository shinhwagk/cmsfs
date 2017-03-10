package org.wex.cmsfs.notification.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}

class NotificationServiceImpl()(implicit ec: ExecutionContext) extends NotificationService {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushNotificationItem: ServiceCall[NotificationItem, Done] = ServiceCall { ni =>
    Future.successful(Done)
  }
}