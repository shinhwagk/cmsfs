package org.wex.cmsfs.collecting.impl

import akka.Done
import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.collecting.api.{CollectItem, CollectingService}

import scala.concurrent.{ExecutionContext, Future}

class CollectingServiceImpl(pubSub: PubSubRegistry)(implicit ec: ExecutionContext, materializer: Materializer) extends CollectingService {
  val topic = pubSub.refFor(TopicId[CollectItem])

  override def pushCollectItem: ServiceCall[CollectItem, Done] = ServiceCall { item =>
    topic.publish(item); Future.successful(Done)
  }
}
