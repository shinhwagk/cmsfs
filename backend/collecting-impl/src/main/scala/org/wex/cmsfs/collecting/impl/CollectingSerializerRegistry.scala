package org.wex.cmsfs.collecting.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.wex.cmsfs.collecting.api.CollectItem

import scala.collection.immutable.Seq

object CollectingSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = List(
  //state
  JsonSerializer[CollectItem]
//    ,

  //commands and replies
//  JsonSerializer[CreateCollectItem],
//
//  //events
//  JsonSerializer[CollectItemCreated]
  )

}
