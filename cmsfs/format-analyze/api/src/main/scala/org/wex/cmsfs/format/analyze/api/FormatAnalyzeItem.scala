package org.wex.cmsfs.format.analyze.api

import play.api.libs.json.{Format, Json}

case class FormatAnalyzeItem(id: Int, _type: String, _index: String,
                             _metric: String, utcDate: String,
                             collectResult: String, path: String,
                             args: String, formatResult: Option[String] = None)

object FormatAnalyzeItem {
  implicit val format: Format[FormatAnalyzeItem] = Json.format
}