package org.wex.cmsfs.format.alarm.api

import org.wex.cmsfs.common.`object`.CoreFormatAlarm
import play.api.libs.json.{Format, Json}

//case class FormatAlarmItem(_type: String, _index: String,
//                           _metric: String, utcDate: String,
//                           collectResult: String, path: String,
//                           args: String, formatResult: Option[String] = None)
//
//object FormatAlarmItem {
//  implicit val format: Format[FormatAlarmItem] = Json.format
//}

case class FormatAlarmItem(id: Int, collectResult: String, coreFormatAlarm: CoreFormatAlarm)

object FormatAlarmItem {
  implicit val format: Format[FormatAlarmItem] = Json.format
}