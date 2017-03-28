package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreFormatAnalyze(id: Int, path: String, args: Option[String], elasticsearch: CoreElasticsearch)

object CoreFormatAnalyze {
  implicit val format: Format[CoreFormatAnalyze] = Json.format
}