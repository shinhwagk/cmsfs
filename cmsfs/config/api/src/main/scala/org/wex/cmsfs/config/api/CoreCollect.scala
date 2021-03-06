package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreCollect(id: Option[Int], path: String, name: String, args: Option[String])

object CoreCollect extends ((Option[Int], String, String, Option[String]) => CoreCollect) {
  implicit val format: Format[CoreCollect] = Json.format
}