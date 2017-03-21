package org.wex.cmsfs.monitor.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.monitor.api.{CollectResult, MonitorService}

import scala.concurrent.{ExecutionContext, Future}

class MonitorServiceImpl(mt: MonitorTopic)(implicit ec: ExecutionContext) extends MonitorService {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushCollectResult: ServiceCall[CollectResult, Done] = ServiceCall { cr =>
    logger.info(s"collect result receive: ${cr.monitorDetailId}")
    mt.collectResultTopic.publish(cr); Future.successful(Done)
  }
}