package org.wex.cmsfs.format.analyze.api

import org.wex.cmsfs.common.`object`.CoreFormatAnalyze
import play.api.libs.json.{Format, Json}

case class FormatAnalyzeItem(id: Int, _metric: String, utcDate: String, collectResult: String, coreFormatAnalyze: CoreFormatAnalyze)

object FormatAnalyzeItem {
  implicit val format: Format[FormatAnalyzeItem] = Json.format
}