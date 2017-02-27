package org.wex.cmsfs.format.api.format

import play.api.libs.json.{Format, Json}

case class AlarmItem(detailId: Long, metricName: String, data: String, args: Seq[String])

object AlarmItem {
  implicit val format: Format[AlarmItem] = Json.format
}