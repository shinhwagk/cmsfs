package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreFormatAlarm(id: Option[Int], path: String, name: String, args: String, collectId: Int)

object CoreFormatAlarm extends ((Option[Int], String, String, String, Int) => CoreFormatAlarm) {
  implicit val format: Format[CoreFormatAlarm] = Json.format
}