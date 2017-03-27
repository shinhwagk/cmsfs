package org.wex.cmsfs.monitor.status.impl

import java.util.Date

import play.api.libs.json.{Format, Json}

case class CoreMonitorStatusAnalyze(status: Boolean, timestamp: String = (new Date()).toString, Error: Option[String] = None)

object CoreMonitorStatusAnalyze {
  implicit val format: Format[CoreMonitorStatusAnalyze] = Json.format
}