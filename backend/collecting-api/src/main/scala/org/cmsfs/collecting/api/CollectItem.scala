package org.cmsfs.collecting.api

import play.api.libs.json.{Format, Json}

case class CollectItem(id: Int)

object CollectItem {
  implicit val format: Format[CollectItem] = Json.format
}