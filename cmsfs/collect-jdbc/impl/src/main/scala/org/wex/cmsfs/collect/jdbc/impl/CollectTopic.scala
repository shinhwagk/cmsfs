package org.wex.cmsfs.collect.jdbc.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.common.`object`.CoreMonitorDetailForJdbc

class CollectTopic(pubSub: PubSubRegistry) {

//  val CollectTopic = pubSub.refFor(TopicId[CollectItemJdbc])
  val CollectTopic = pubSub.refFor(TopicId[CoreMonitorDetailForJdbc])

}
