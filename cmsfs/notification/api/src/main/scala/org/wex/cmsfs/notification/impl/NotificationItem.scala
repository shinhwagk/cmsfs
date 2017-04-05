package org.wex.cmsfs.notification.impl

import play.api.libs.json.{Format, Json}

case class NotificationItem(category: String, target: String, content: String)

object NotificationItem {
  implicit val format: Format[NotificationItem] = Json.format
}

case class NotificationItemTest(appId: String, orderNo: String, protocol: String, targetIdenty: String, targetCount: String, content: String, isRealTime: String)

object NotificationItemTest {
  implicit val format: Format[NotificationItemTest] = Json.format
}
