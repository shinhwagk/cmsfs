package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreMonitorStatus(id: Option[Int],
                             category: String, metric: String,
                             collectState: Boolean, collectTimestamp: String, collectError: Option[String],
                             analyzeState: Option[Boolean], analyzeTimestamp: Option[String], analyzeError: Option[String],
                             alarmState: Option[Boolean], alarmTimestamp: Option[String], alarmError: Option[String])

object CoreMonitorStatus extends ((Option[Int],
  String, String,
  Boolean, String, Option[String],
  Option[Boolean], Option[String], Option[String],
  Option[Boolean], Option[String], Option[String]) => CoreMonitorStatus) {
  implicit val format: Format[CoreMonitorStatus] = Json.format
}