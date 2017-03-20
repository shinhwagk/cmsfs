package org.wex.cmsfs.collect.ssh.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.ssh.api.{CollectItemSsh, CollectSSHService}

import scala.concurrent.{ExecutionContext, Future}

class CollectSSHServiceImpl(ct: CollectTopic)(implicit ec: ExecutionContext) extends CollectSSHService {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushCollectItem: ServiceCall[CollectItemSsh, Done] = ServiceCall { ci =>
    logger.info(s"collect ssh receive: ${ci.monitorDetailId}-${ci.collect.name}")
    ct.CollectTopic.publish(ci); Future.successful(Done)
  }
}