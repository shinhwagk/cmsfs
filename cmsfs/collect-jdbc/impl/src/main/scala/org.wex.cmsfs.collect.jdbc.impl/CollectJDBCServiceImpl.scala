package org.wex.cmsfs.collect.jdbc.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.jdbc.api.{CollectItemJDBC, CollectJDBCService}

import scala.concurrent.{ExecutionContext, Future}

class CollectJDBCServiceImpl(ct: CollectTopic)(implicit ec: ExecutionContext) extends CollectJDBCService {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushCollectItem: ServiceCall[CollectItemJDBC, Done] = ServiceCall { ci =>
    logger.info(s"collect jdbc receive: ${ci.id}-${ci.metricName}")
    ct.CollectTopic.publish(ci); Future.successful(Done)
  }
}