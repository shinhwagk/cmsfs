package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreMonitorDetailForSsh(id: Int, utcDate: String,
                                   connector: CoreConnectorSsh, collect: CoreCollect,
                                   analyze: Option[CoreFormatAnalyze],
                                   alarm: Option[CoreFormatAlarm])

object CoreMonitorDetailForSsh {
  implicit val format: Format[CoreMonitorDetailForSsh] = Json.format
}