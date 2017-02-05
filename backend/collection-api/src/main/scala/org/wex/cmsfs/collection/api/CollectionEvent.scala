package org.wex.cmsfs.collection.api

import java.util.UUID

import julienrf.json.derived
import play.api.libs.json._

sealed trait CollectionEvent {
  val id: UUID
}

case class CollectionCreated(id: UUID, monitorId: Int, connectorId: Int) extends CollectionEvent

object CollectionCreated {
  implicit val format: Format[CollectionCreated] = Json.format
}

case class CollectionItemCompleted(id: UUID, result: String) extends CollectionEvent

object CollectionItemCompleted {
  implicit val format: Format[CollectionItemCompleted] = Json.format
}

object CollectionEvent {
  implicit val format: Format[CollectionEvent] =
    derived.flat.oformat((__ \ "type").format[String])
}


