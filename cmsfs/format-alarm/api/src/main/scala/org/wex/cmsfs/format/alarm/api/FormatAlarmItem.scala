package org.wex.cmsfs.format.alarm.api

import org.wex.cmsfs.common.`object`.CoreFormatAlarm
import play.api.libs.json.{Format, Json}

case class FormatAlarmItem(id: Int, collectResult: String, coreFormatAlarm: CoreFormatAlarm)

object FormatAlarmItem {
  implicit val format: Format[FormatAlarmItem] = Json.format
}