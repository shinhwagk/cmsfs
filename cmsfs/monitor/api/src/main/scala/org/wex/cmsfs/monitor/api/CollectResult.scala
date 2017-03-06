package org.wex.cmsfs.monitor.api

import play.api.libs.json.{Format, Json}

case class CollectResult(id: Long, rs: Option[String])

object CollectResult {
  implicit val format: Format[CollectResult] = Json.format
}