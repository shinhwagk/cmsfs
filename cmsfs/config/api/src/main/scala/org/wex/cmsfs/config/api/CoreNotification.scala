package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreNotification(mails: Seq[String], mobiles: Seq[String])

object CoreNotification {
  implicit val format: Format[CoreNotification] = Json.format
}