package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreFormatAlarm(id: Option[Int], path: String,  args: String, notification: String)

object CoreFormatAlarm extends ((Option[Int], String,  String, String) => CoreFormatAlarm) {
  implicit val format: Format[CoreFormatAlarm] = Json.format
}