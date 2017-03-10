package org.wex.cmsfs.format.alarm.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.format.alarm.api.FormatAlarmItem

class FormatAlarmTopic(pubSub: PubSubRegistry) {
  val formatTopic = pubSub.refFor((TopicId[FormatAlarmItem]))
}
