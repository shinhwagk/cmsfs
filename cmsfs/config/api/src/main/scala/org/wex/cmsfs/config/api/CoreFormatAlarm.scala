package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreFormatAlarm(id: Option[Int], path: String, name: String, args: Option[String], notification: String)

object CoreFormatAlarm extends ((Option[Int], String, String, Option[String], String) => CoreFormatAlarm) {
  implicit val format: Format[CoreFormatAlarm] = Json.format
}