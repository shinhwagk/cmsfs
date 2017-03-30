package ogr.wex.cmsfs.monitor.api

import play.api.libs.json.{Format, Json}

case class MonitorStatusItem(key: String, status: List[String])

object MonitorStatusItem {
  implicit val format: Format[MonitorStatusItem] = Json.format
}