package org.wex.cmsfs.format.api

import play.api.libs.json.{Format, Json}

case class FormatItem(result:String,formatId:Int)

object FormatItem {
  implicit val format: Format[FormatItem] = Json.format
}