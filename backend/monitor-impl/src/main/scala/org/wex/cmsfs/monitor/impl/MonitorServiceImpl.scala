package org.wex.cmsfs.monitor.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.monitor.api.{MonitorItem, MonitorService}

import scala.concurrent.{ExecutionContext, Future}

class MonitorServiceImpl(pubSub: PubSubRegistry)(implicit ec: ExecutionContext) extends MonitorService {
  val topic = pubSub.refFor(TopicId[MonitorItem])

  override def pushMonitorItem: ServiceCall[MonitorItem, Done] = ServiceCall { item =>
    topic.publish(item); Future.successful(Done)
  }
}
