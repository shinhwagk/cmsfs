package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreFormatAnalyze(id: Option[Int], path: String, args: Option[String], _index: String, _type: String, _metric: String)

object CoreFormatAnalyze extends ((Option[Int], String, Option[String],  String, String, String) => CoreFormatAnalyze) {
  implicit val format: Format[CoreFormatAnalyze] = Json.format
}