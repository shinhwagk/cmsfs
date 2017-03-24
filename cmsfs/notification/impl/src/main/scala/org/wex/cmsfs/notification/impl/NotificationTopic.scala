package org.wex.cmsfs.notification.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}

class NotificationTopic(pubSub: PubSubRegistry) {
  val CollectTopic = pubSub.refFor(TopicId[NotificationItem])
}