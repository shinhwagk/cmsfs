package org.wex.cmsfs.format.analyze.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem, FormatAnalyzeItem2, FormatAnalyzeService}

import scala.concurrent.{ExecutionContext, Future}

class FormatAnalyzeServiceImpl(topic: FormatAnalyzeTopic)(implicit ec: ExecutionContext) extends FormatAnalyzeService {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushFormatAnalyze: ServiceCall[FormatAnalyzeItem, Done] = ServiceCall { fai =>
    logger.info(s"format analyze receive: ${fai._metric}")
    Future.successful(Done)
  }

  override def pushFormatAnalyze2: ServiceCall[FormatAnalyzeItem2, Done] = ServiceCall { fai =>
    logger.info(s"format analyze receive: ${fai._metric}")
    topic.formatTopic.publish(fai);
    Future.successful(Done)
  }
}