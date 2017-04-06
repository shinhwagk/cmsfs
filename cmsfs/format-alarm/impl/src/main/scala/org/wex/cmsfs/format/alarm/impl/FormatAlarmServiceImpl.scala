package org.wex.cmsfs.format.alarm.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.format.alarm.api.{FormatAlarmItem, FormatAlarmService}

import scala.concurrent.{ExecutionContext, Future}

class FormatAlarmServiceImpl(topic: FormatAlarmTopic)(implicit ec: ExecutionContext) extends FormatAlarmService {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushFormatAlarm: ServiceCall[FormatAlarmItem, Done] = ServiceCall { fai =>
    logger.info(s"format alarm receive: ${fai.id}")
    topic.formatTopic.publish(fai);
    Future.successful(Done)
  }
}