package org.wex.cmsfs.collect.ssh.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.ssh.api.{CollectItemSsh, CollectSSHService}
import org.wex.cmsfs.common.`object`.CoreMonitorDetailForSsh
import scala.concurrent.{ExecutionContext, Future}

class CollectSSHServiceImpl(ct: CollectTopic)(implicit ec: ExecutionContext) extends CollectSSHService {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushCollectItem: ServiceCall[CoreMonitorDetailForSsh, Done] = ServiceCall{ cmdfj =>
    logger.info(s"collect ssh receive: ${cmdfj.id}-${cmdfj.collect.name}")
    ct.CollectTopic.publish(cmdfj);
    Future.successful(Done)
  }
}