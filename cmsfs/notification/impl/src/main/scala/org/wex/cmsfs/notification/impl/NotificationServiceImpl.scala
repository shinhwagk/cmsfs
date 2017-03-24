package org.wex.cmsfs.notification.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.{ExecutionContext, Future}

class NotificationServiceImpl(nt: NotificationTopic)(implicit ec: ExecutionContext) extends NotificationService {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushNotificationItem: ServiceCall[NotificationItem, Done] = ServiceCall { ni =>
    logger.info(s"notification receive: ${ni.category}/${ni.target}")
    nt.CollectTopic.publish(ni); Future.successful(Done)
  }
}