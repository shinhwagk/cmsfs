package org.wex.cmsfs.collect.jdbc.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.jdbc.api.{CollectItemJdbc, CollectJDBCService}
import org.wex.cmsfs.common.`object`.CoreMonitorDetailForJdbc

import scala.concurrent.{ExecutionContext, Future}

class CollectJDBCServiceImpl(ct: CollectTopic)(implicit ec: ExecutionContext) extends CollectJDBCService {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushCollectItem: ServiceCall[CollectItemJdbc, Done] = ServiceCall { ci =>
    logger.info(s"collect jdbc receive: ${ci.monitorDetailId}-${ci.collect.name}")
//    ct.CollectTopic.publish(ci);
    Future.successful(Done)
  }

  override def pushCollectItem2: ServiceCall[CoreMonitorDetailForJdbc, Done] = ServiceCall { cmdfj =>
    logger.info(s"collect jdbc receive: ${cmdfj.id}-${cmdfj.collect.name}")
    ct.CollectTopic.publish(cmdfj);
    Future.successful(Done)
  }
}