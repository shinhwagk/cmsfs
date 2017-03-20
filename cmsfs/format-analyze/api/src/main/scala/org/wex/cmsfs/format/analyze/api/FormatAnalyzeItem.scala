package org.wex.cmsfs.format.analyze.api

import org.wex.cmsfs.config.api.CoreFormatAnalyze
import org.wex.cmsfs.monitor.api.CollectResult
import play.api.libs.json.{Format, Json}

case class FormatAnalyzeItem(coreFormatAnalyze: CoreFormatAnalyze, collectResult: CollectResult)

object FormatAnalyzeItem {
  implicit val format: Format[FormatAnalyzeItem] = Json.format
}