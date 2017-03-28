package org.wex.cmsfs.collect.ssh.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.common.`object`.CoreMonitorDetailForSsh

class CollectTopic(pubSub: PubSubRegistry) {
  val CollectTopic = pubSub.refFor(TopicId[CoreMonitorDetailForSsh])
}
