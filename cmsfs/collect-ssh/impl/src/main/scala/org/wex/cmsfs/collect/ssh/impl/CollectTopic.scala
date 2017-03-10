package org.wex.cmsfs.collect.ssh.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.collect.ssh.api.CollectItemSSH
import play.api.Configuration

class CollectTopic(pubSub: PubSubRegistry) {
  val CollectTopic = pubSub.refFor(TopicId[CollectItemSSH])

}
