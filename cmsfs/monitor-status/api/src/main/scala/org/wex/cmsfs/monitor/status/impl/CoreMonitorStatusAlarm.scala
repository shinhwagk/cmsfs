package org.wex.cmsfs.monitor.status.impl

import java.util.Date
import play.api.libs.json.{Format, Json}

case class CoreMonitorStatusAlarm(status: Boolean, timestamp: String = (new Date()).toString, Error: Option[String] = None)

object CoreMonitorStatusAlarm {
  implicit val format: Format[CoreMonitorStatusAlarm] = Json.format
}