package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

/**
  *
  * eg: Map(mail -> seq(1888888@qq.com),phone -> seq(13818888888))
  *
  */
case class CoreNotification(args: String, sends: Map[String, Seq[String]])

object CoreNotification {
  implicit val format: Format[CoreNotification] = Json.format
}