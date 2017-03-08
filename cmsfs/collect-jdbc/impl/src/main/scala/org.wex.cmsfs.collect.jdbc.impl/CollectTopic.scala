package org.wex.cmsfs.collect.jdbc.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.collect.jdbc.api.CollectItemJDBC

class CollectTopic(pubSub: PubSubRegistry) {

  val CollectTopic = pubSub.refFor(TopicId[CollectItemJDBC])

}
