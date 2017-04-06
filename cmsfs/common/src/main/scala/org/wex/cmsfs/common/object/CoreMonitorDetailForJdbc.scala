package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreMonitorDetailForJdbc(id: Int, utcDate: String,
                                    connector: CoreConnectorJdbc, collect: CoreCollect,
                                    analyze: Option[CoreFormatAnalyze],
                                    alarms: Seq[CoreFormatAlarm])

object CoreMonitorDetailForJdbc {
  implicit val format: Format[CoreMonitorDetailForJdbc] = Json.format
}