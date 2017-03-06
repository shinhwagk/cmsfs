package org.wex.cmsfs.collect.ssh.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.collect.ssh.api.CollectItemSSH

class CollectTopic(pubSub: PubSubRegistry) {

  val CollectTopicSSH = pubSub.refFor(TopicId[CollectItemSSH])

}