package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreMonitorDetail(id: Option[Int], cron: String,
                             connectorMode: String, connectorId: Int,
                             collectId: Int, collectArgs: Option[String],
                             formatAnalyzeId: Option[Int], formatAnalyzeArgs: Option[String],
                             formatAlarmIds: Seq[Int])

object CoreMonitorDetail extends ((Option[Int], String, String, Int, Int, Option[String], Option[Int], Option[String], Seq[Int]) => CoreMonitorDetail) {
  implicit val format: Format[CoreMonitorDetail] = Json.format
}