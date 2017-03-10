package org.wex.cmsfs.format.analyze

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeItem

class FormatAnalyzeTopic(pubSub: PubSubRegistry) {
  val formatTopic = pubSub.refFor((TopicId[FormatAnalyzeItem]))
}
