package org.wex.cmsfs.monitor.api

import play.api.libs.json.{Format, Json}

case class MonitorActionDepository(id: Long, monitorId: Long, metricName: String, collectData: String, analyze: Option[String] = None)

object MonitorActionDepository {
  implicit val format: Format[MonitorActionDepository] = Json.format
}