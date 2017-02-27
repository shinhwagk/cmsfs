package org.wex.cmsfs.collection.api

import java.util.UUID
import com.lightbend.lagom.scaladsl.api.deser.PathParamSerializer
import play.api.libs.json.{Format, Json}
import JsonFormats._

case class Collection(id: Option[UUID],
                      cron: String,
                      mode: String,
                      monitorId: Int,
                      connectorId: Int,
                      status: CollectionStatus.Status){
  def withStatus (status: CollectionStatus.Status): Collection = copy(status = status)
  def withId (id:UUID) = copy(id = Some(id))
}

object Collection {
  implicit val format: Format[Collection] = Json.format
}

object CollectionStatus extends Enumeration {
  val Created, Collecting, Completed, Cancelled = Value
  type Status = Value

  implicit val format: Format[Value] = enumFormat(this)
  implicit val pathParamSerializer: PathParamSerializer[Status] =
    PathParamSerializer.required("CollectionState")(withName)(_.toString)
}