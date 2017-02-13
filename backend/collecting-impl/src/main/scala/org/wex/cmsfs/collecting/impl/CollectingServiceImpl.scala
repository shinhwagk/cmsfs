package org.wex.cmsfs.collecting.impl

import akka.Done
import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.cmsfs.collecting.api.{CollectItem, CollectingService}

import scala.concurrent.{ExecutionContext, Future}

class CollectingServiceImpl(registry: PersistentEntityRegistry, pubSub: PubSubRegistry)(implicit ec: ExecutionContext, materializer: Materializer) extends CollectingService {
  val topic = pubSub.refFor(TopicId[CollectItem])

  override def createCollectItem: ServiceCall[CollectItem, CollectItem] = ServiceCall { c =>
    entityRef(c.id).ask(CreateCollectItem(c)).map(_ => c)
  }

  private def entityRef(id: Int) = entityRefString(id.toString)

  private def entityRefString(id: String) = registry.refFor[CollectingEntity](id)

  override def pushCollectItem: ServiceCall[CollectItem, Done] = ServiceCall { item =>
    topic.publish(item); Future.successful(Done)
  }
}
