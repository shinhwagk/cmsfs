package org.wex.cmsfs.monitor.api

import play.api.libs.json.{Format, Json}

case class CollectResult(id: Int, metricName: String, rs: Option[String], utcDate: String, name: String)

object CollectResult {
  implicit val format: Format[CollectResult] = Json.format
}