package org.wex.cmsfs.format.alarm.api

import play.api.libs.json.{Format, Json}

case class FormatAlarmItem(id: Long, metricName: String, data: String, args: String)

object FormatAlarmItem {
  implicit val format: Format[FormatAlarmItem] = Json.format
}