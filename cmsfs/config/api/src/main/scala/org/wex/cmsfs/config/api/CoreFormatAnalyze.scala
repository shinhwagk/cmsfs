package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreFormatAnalyze(id: Option[Int], path: String, name: String, args: String, collectId: Int)

object CoreFormatAnalyze extends ((Option[Int], String, String, String, Int) => CoreFormatAnalyze) {
  implicit val format: Format[CoreFormatAnalyze] = Json.format
}