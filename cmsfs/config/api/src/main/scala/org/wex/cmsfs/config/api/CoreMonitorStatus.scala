package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreMonitorStatus(id: Int, category: String, name: String, metric: String,
                             collect: Option[CoreMonitorStageStatus] = None,
                             analyze: Option[CoreMonitorStageStatus] = None,
                             alarm: Option[CoreMonitorStageStatus] = None)

object CoreMonitorStatus extends ((Int, String, String, String,
  Option[CoreMonitorStageStatus], Option[CoreMonitorStageStatus], Option[CoreMonitorStageStatus]) => CoreMonitorStatus) {
  implicit val format: Format[CoreMonitorStatus] = Json.format
}

case class CoreMonitorStageStatus(state: Boolean, timestamp: String, result: String)

object CoreMonitorStageStatus {
  implicit val format: Format[CoreMonitorStageStatus] = Json.format
}