package org.wex.cmsfs.format.api.format

import play.api.libs.json.{Format, Json}

case class AnalyzeItem(detailId: Long, metricName: String, data: String, args: Seq[String])

object AnalyzeItem {
  implicit val format: Format[AnalyzeItem] = Json.format
}
