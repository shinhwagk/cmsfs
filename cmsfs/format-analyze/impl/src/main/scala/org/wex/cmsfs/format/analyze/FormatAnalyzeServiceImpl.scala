package org.wex.cmsfs.format.analyze

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem, FormatAnalyzeService}

import scala.concurrent.{ExecutionContext, Future}

class FormatAnalyzeServiceImpl(topic: FormatAnalyzeTopic)(implicit ec: ExecutionContext) extends FormatAnalyzeService {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushFormatAnalyze: ServiceCall[FormatAnalyzeItem, Done] = ServiceCall { fai =>
    logger.info(s"format alarm receive: ${fai.id}-${fai.metricName}")
    topic.formatTopic.publish(fai);
    Future.successful(Done)
    //    ct.CollectTopic.publish(ci);
  }
}