package org.wex.cmsfs.collection.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import org.wex.cmsfs.collection.api.{Collection, CollectionStatus, JsonFormats}
import play.api.libs.json.{Format, Json}

class CollectionEntity extends PersistentEntity {
  override type Command = CollectionCommand
  override type Event = CollectionEvent
  override type State = Option[Collection]

  override def initialState: Option[Collection] = None

  override def behavior: Behavior = {
    case None => notCreated
    case Some(item) if item.status == CollectionStatus.Created => {
      println(111)
      created(item)
    }
    case Some(collection) if collection.status == CollectionStatus.Collecting => Collecting(collection)
    case Some(collection) if collection.status == CollectionStatus.Completed => completed
    case Some(collection) if collection.status == CollectionStatus.Cancelled => completed
  }

  private val getItemCommand = Actions().onReadOnlyCommand[GetCollection.type, Collection] {
    case (GetCollection, ctx, state) => ctx.reply(state.get)
  }

  private val notCreated = {
    Actions().onCommand[CreateCollection, Done] {
      case (CreateCollection(cItem), ctx, _) =>
        println("notcreated")
        ctx.thenPersist(CollectionItemCreated(cItem))(_ => ctx.reply(Done))
    }.onEvent {
      case (CollectionItemCreated(item), _) =>
        println("notcreated event", item, "9999999999999")
        Some(item.withStatus(CollectionStatus.Created))
    }.orElse(getItemCommand)
  }

  private def created(item: Collection) = {
    Actions().onCommand[CreateCollection, Done] {
      case (StartCollection(cItem), ctx, _) =>
        println(item, "StartCollection")
        ctx.thenPersist(CollectionItemStarted(cItem))(_ => ctx.reply(Done))
    }.onCommand[RecordCollection, Done] {
      case (RecordCollection(result), ctx, state) =>
        println(state, 99)
        ctx.thenPersist(CollectionItemCompleted(item, result))(_ => ctx.reply(Done))
    }.onEvent {
      case (CollectionItemCompleted(_, _), _) => Some(item.withStatus(CollectionStatus.Completed))
      case (CollectionItemStarted(_), Some(item)) => {
        println(item, "collectionItemStarted")
        Some(item.withStatus(CollectionStatus.Collecting))
      }
      case _ => {
        println("none")
        Some(item.withStatus(CollectionStatus.Completed))
      }
    }.orElse(getItemCommand)
  }

  private def Collecting(item: Collection) = {
    println("Collecting")
    Actions().onCommand[RecordCollection, Done] {
      case (RecordCollection(result), ctx, state) =>
        println(state, 99)
        ctx.thenPersist(CollectionItemCompleted(item, result))(_ => ctx.reply(Done))
    }.onEvent {
      case (CollectionItemCompleted(_, _), _) => Some(item.withStatus(CollectionStatus.Completed))
    }
  }.orElse(getItemCommand)

  private val completed = {
    Actions().onCommand[RecordCollection, Done] {
      case (StartCollection(citem), ctx, state) =>
        ctx.thenPersist(CollectionItemStarted(citem))(_ => ctx.reply(Done))
    }.onEvent {
      case (CollectionItemStarted(item), _) => Some(item.withStatus(CollectionStatus.Collecting))
    }
  }.orElse(getItemCommand)
}

case object GetCollection extends CollectionCommand with ReplyType[Collection] {
  implicit val format: Format[GetCollection.type] = JsonFormats.singletonFormat(GetCollection)
}

sealed trait CollectionCommand

case class CreateCollection(cItem: Collection) extends CollectionCommand with ReplyType[Done]

object CreateCollection {
  implicit val format: Format[CreateCollection] = Json.format
}

case class StartCollection(cItem: Collection) extends CollectionCommand with ReplyType[Done]

object StartCollection {
  implicit val format: Format[CreateCollection] = Json.format
}

case class RecordCollection(result: String) extends CollectionCommand with ReplyType[Done]

object RecordCollection {
  implicit val format: Format[RecordCollection] = Json.format
}

sealed trait CollectionEvent extends AggregateEvent[CollectionEvent] {
  override def aggregateTag = CollectionEvent.Tag
}

object CollectionEvent {
  val NumShards = 2
  val Tag = AggregateEventTag.sharded[CollectionEvent](NumShards)
}

case class CollectionItemCreated(cItem: Collection) extends CollectionEvent

object CollectionItemCreated {
  implicit val format: Format[CollectionItemCreated] = Json.format
}


case class CollectionItemStarted(cItem: Collection) extends CollectionEvent

object CollectionItemStarted {
  implicit val format: Format[CollectionItemCreated] = Json.format
}

case class CollectionItemCompleted(cItem: Collection, result: String) extends CollectionEvent

object CollectionItemCompleted {
  implicit val format: Format[CollectionItemCompleted] = Json.format
}