package org.wex.cmsfs.collection.impl

import java.util.UUID

import akka.NotUsed
import akka.persistence.query.Offset
import com.datastax.driver.core.utils.UUIDs
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import org.wex.cmsfs.collection.api
import org.wex.cmsfs.collection.api.{Collection, CollectionService}

import scala.concurrent.{ExecutionContext, Future}

class CollectionServiceImpl(registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends CollectionService {

  override def createCollection: ServiceCall[Collection, Collection] = ServiceCall { collection =>
    val collectionId = UUIDs.timeBased()
    entityRef(collectionId).ask(CreateCollection(collection.withId(collectionId))).map(_ => collection.withId(collectionId))
  }

  override def getCollectionById(id: String): ServiceCall[NotUsed, Collection] = ServiceCall { _ =>
    entityRefString(id).ask(GetCollection)
  }

    override def collectionEvents = TopicProducer.taggedStreamWithOffset(CollectionEvent.Tag.allTags.toList) { (tag, offset) =>
      println(111111112)
      registry.eventStream(tag, offset).filter {
        _.event match {
          case x@(_: CollectionItemCreated) => {
            println(111111113)
            true
          }
          case _ => false
        }
      }.mapAsync(1)(convertEvent)
    }

  //  override def collectionCompletedEvents = TopicProducer.taggedStreamWithOffset(CollectionEvent.Tag.allTags.toList) { (tag, offset) =>
  //    registry.eventStream(tag, offset).filter {
  //      _.event match {
  //        case x@(_: CollectionItemCompleted) => true
  //        case _ => false
  //      }
  //    }.mapAsync(1)(convertEvent)
  //  }

  private def convertEvent(eventStreamElement: EventStreamElement[CollectionEvent]): Future[(api.CollectionEvent, Offset)] = {
    eventStreamElement match {
      case EventStreamElement(_, CollectionItemCreated(coll), offset) => {
        println(11114)
        Future((api.CollectionCreated(coll.id.get, coll.monitorId, coll.connectorId), offset))
      }
//      case EventStreamElement(_, CollectionItemCompleted(coll, result), offset) => {
//        Future((api.CollectionItemCompleted(coll.id.get, result), offset))
//      }
    }
  }

  private def entityRef(itemId: UUID) = entityRefString(itemId.toString)

  private def entityRefString(itemId: String) = registry.refFor[CollectionEntity](itemId)

}
