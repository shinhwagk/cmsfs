package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreMonitorStatus(id: Int, category: String, metric: String, status: String)

object CoreMonitorStatus extends ((Int, String, String, String) => CoreMonitorStatus) {
  implicit val format: Format[CoreMonitorStatus] = Json.format
}