package org.wex.cmsfs.monitor.status.impl

import java.util.Date

import play.api.libs.json.{Format, Json}

case class CoreMonitorStatusCollect(status: Boolean, timestamp: String = (new Date()).toString, Error: Option[String] = None)

object CoreMonitorStatusCollect {
  implicit val format: Format[CoreMonitorStatusCollect] = Json.format
}