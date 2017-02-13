package org.wex.cmsfs.collecting.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import org.cmsfs.collecting.api.CollectItem
import play.api.libs.json.{Format, Json}

sealed trait CollectingEvent extends AggregateEvent[CollectingEvent] {
  override def aggregateTag = CollectingEvent.Tag
}

object CollectingEvent {
  val NumShards = 1
  val Tag = AggregateEventTag.sharded[CollectingEvent](NumShards)
}

case class CollectItemCreated(collecting: CollectItem) extends CollectingEvent

object CollectItemCreated {
  implicit val format: Format[CollectItemCreated] = Json.format
}
