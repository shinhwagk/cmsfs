package org.wex.cmsfs.format.impl

import akka.Done
import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.LoggerFactory
import org.wex.cmsfs.format.api.FormatService
import org.wex.cmsfs.format.api.format.{AlarmItem, AnalyzeItem}

import scala.concurrent.{ExecutionContext, Future}

class FormatServiceImpl(ft: FormatTopic)(implicit ec: ExecutionContext, mi: Materializer) extends FormatService {

//  private val log = LoggerFactory.getLogger(classOf[FormatServiceImpl])

  override def pushFormatAnalyze: ServiceCall[AnalyzeItem, Done] = ServiceCall { ai =>
//    log.info("接受analyze")
    println(s"debug: push format analyze ${ai.metricName}")
    ft.analyzeTopic.publish(ai);
    Future.successful(Done)
  }

  override def pushFormatAlarm: ServiceCall[AlarmItem, Done] = ServiceCall { ai =>
    ft.alarmItem.publish(ai);
    Future.successful(Done)
  }

}
