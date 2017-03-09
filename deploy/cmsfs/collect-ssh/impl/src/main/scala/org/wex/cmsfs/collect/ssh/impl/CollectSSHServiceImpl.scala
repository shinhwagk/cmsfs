package org.wex.cmsfs.collect.ssh.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.collect.ssh.api.{CollectItemSSH, CollectSSHService}

import scala.concurrent.{ExecutionContext, Future}

class CollectSSHServiceImpl(ct: CollectTopic)(implicit ec: ExecutionContext) extends CollectSSHService {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def pushCollectItem: ServiceCall[CollectItemSSH, Done] = ServiceCall { cSSH =>
    logger.info(s"collect ssh receive: ${cSSH.id}-${cSSH.metricName}")
    ct.CollectTopicSSH.publish(cSSH); Future.successful(Done)
  }
}