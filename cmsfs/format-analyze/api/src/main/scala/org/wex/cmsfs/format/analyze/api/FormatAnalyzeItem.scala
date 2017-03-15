package org.wex.cmsfs.format.analyze.api

import play.api.libs.json.{Format, Json}

case class FormatAnalyzeItem(id: Long, metricName: String, data: String, args: String, utcDate: String)

object FormatAnalyzeItem {
  implicit val format: Format[FormatAnalyzeItem] = Json.format
}