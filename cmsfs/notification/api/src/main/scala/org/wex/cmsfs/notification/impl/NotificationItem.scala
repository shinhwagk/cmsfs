package org.wex.cmsfs.notification.impl

import play.api.libs.json.{Format, Json}

case class NotificationItem(category: String, target: String, content: String)

object NotificationItem {
  implicit val format: Format[NotificationItem] = Json.format
}


