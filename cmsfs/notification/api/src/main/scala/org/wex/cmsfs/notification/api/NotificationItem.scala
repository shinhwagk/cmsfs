package org.wex.cmsfs.notification.api

import play.api.libs.json.{Format, Json}

case class NotificationItem(content: String, category: String, target: String)

object NotificationItem {
  implicit val format: Format[NotificationItem] = Json.format
}


