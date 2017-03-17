package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreMonitorDetail(id: Option[Int], cron: String, category: String, connector_id: Int, collectId: Int)

object CoreMonitorDetail extends ((Option[Int], String, String, Int, Int) => CoreMonitorDetail) {
  implicit val format: Format[CoreMonitorDetail] = Json.format
}