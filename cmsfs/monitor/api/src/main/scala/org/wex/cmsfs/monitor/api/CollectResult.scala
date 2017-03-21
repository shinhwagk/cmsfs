package org.wex.cmsfs.monitor.api

import play.api.libs.json.{Format, Json}

case class CollectResult(monitorDetailId: Int, metricName: String, rs: Option[String], utcDate: String, connectorName: String)

object CollectResult {
  implicit val format: Format[CollectResult] = Json.format
}