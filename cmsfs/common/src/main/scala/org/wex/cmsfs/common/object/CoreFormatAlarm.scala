package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreFormatAlarmNotification(mails: Seq[String], mobiles: Seq[String])

object CoreFormatAlarmNotification {
  implicit val format: Format[CoreFormatAlarmNotification] = Json.format
}

case class CoreFormatAlarm(id: Int, path: String, args: String, notification: CoreFormatAlarmNotification)

object CoreFormatAlarm {
  implicit val format: Format[CoreFormatAlarm] = Json.format
}