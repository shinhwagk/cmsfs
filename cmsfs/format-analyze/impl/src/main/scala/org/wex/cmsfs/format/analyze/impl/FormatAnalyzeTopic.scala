package org.wex.cmsfs.format.analyze.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeItem
import org.wex.cmsfs.format.analyze.impl.api.FormatAnalyzeItem

class FormatAnalyzeTopic(pubSub: PubSubRegistry) {
  val formatTopic = pubSub.refFor((TopicId[FormatAnalyzeItem]))
}
