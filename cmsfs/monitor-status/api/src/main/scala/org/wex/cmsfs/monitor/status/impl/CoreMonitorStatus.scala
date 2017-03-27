package org.wex.cmsfs.monitor.status.impl

import play.api.libs.json.{Format, Json}

case class CoreMonitorStatus(id: Int,
                             category: String, name: String, metric: String,
                             collect: Option[CoreMonitorStatusCollect],
                             analyze: Option[CoreMonitorStatusAnalyze],
                             alarm: Option[CoreMonitorStatusAlarm])

object CoreMonitorStatus {
  implicit val format: Format[CoreMonitorStatus] = Json.format
}