//package org.wex.cmsfs.collecting.impl

//import akka.Done
//import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
//import org.cmsfs.collecting.api.CollectItem

//class CollectingEntity extends PersistentEntity {
//  override type Command = CollectingCommand
//  override type Event = CollectingEvent
//  override type State = Option[CollectItem]
//
//  override def initialState: Option[CollectItem] = None
//
//  override def behavior: Behavior = {
//    case None => notCreated
//  }
//
//  val notCreated = {
//    Actions().onCommand[CreateCollectItem, Done] {
//      case (CreateCollectItem(collect), ctx, _) =>
//        ctx.thenPersist(CollectItemCreated(collect))(_ => ctx.reply(Done))
//    }.onEvent {
//      case (CollectItemCreated(collect), _) => Some(collect)
//    }
//  }
//}
