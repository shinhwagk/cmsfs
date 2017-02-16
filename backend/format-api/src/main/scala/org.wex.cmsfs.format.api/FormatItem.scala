package org.wex.cmsfs.format.api

import play.api.libs.json.{Format, Json}

case class FormatItem(collectId: Long, formatId: Int)

object FormatItem {
  implicit val format: Format[FormatItem] = Json.format
}

case class FormatItemAlarm(result: String, formatId: Int)

object FormatItemAlarm {
  implicit val format: Format[FormatItem] = Json.format
}