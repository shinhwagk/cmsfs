package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreCollect(id: Int, name: String, path: String, args: Option[String])

object CoreCollect {
  implicit val format: Format[CoreCollect] = Json.format
}