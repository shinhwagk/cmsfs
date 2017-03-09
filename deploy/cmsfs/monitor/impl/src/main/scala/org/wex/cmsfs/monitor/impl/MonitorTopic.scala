package org.wex.cmsfs.monitor.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.monitor.api.CollectResult

class MonitorTopic(pubSub: PubSubRegistry) {

//  val jdbcCollectTopic = pubSub.refFor(TopicId[MonitorActionForJDBC])

  val collectResultTopic = pubSub.refFor((TopicId[CollectResult]))

}
