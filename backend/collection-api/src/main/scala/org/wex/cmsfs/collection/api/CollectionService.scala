package org.wex.cmsfs.collection.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait CollectionService extends Service {

  def createCollection: ServiceCall[Collection, Collection]

  def getCollectionById(id: String): ServiceCall[NotUsed, Collection]

  def collectionEvents: Topic[CollectionEvent]

  def collectionCompletedEvents: Topic[CollectionEvent]

  final override def descriptor = {
    import Service._

    named("collection").withCalls(
      restCall(Method.POST, "/api/collection", createCollection),
      restCall(Method.GET, "/api/collection/:Id", getCollectionById _)
    ).withTopics(
      topic("collection-CollectionEvent", collectionEvents),
      topic("collection-CollectionCompletedEvent", collectionCompletedEvents)
    )
  }
}
