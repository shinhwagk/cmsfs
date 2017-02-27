package org.wex.cmsfs.collection.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.wex.cmsfs.collection.api.Collection

import scala.collection.immutable.Seq

/**
  * Created by shinhwagk on 2017/2/3.
  */
object CollectionSerializerRegistry extends JsonSerializerRegistry{
  override def serializers: Seq[JsonSerializer[_]] = List(
    //state
    JsonSerializer[Collection],

    // Commands and replies
    JsonSerializer[CreateCollection],

    //events
    JsonSerializer[CollectionItemCreated],
    JsonSerializer[CollectionItemCompleted]
  )
}
