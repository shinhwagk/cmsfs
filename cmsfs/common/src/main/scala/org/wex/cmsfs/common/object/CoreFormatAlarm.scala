package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreFormatAlarm(id: Int, path: String, args: Option[String])

object CoreFormatAlarm {
  implicit val format: Format[CoreFormatAlarm] = Json.format
}