package org.wex.cmsfs.format.analyze.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeItem2

class FormatAnalyzeTopic(pubSub: PubSubRegistry) {
  val formatTopic = pubSub.refFor((TopicId[FormatAnalyzeItem2]))
}
