package org.wex.cmsfs.format.impl

import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.format.api.format.{AlarmItem, AnalyzeItem}
import scala.concurrent.ExecutionContext

class FormatTopic(pubSub: PubSubRegistry)(implicit ec: ExecutionContext, mi: Materializer) {

  val alarmItem = pubSub.refFor(TopicId[AlarmItem])

  val analyzeTopic = pubSub.refFor(TopicId[AnalyzeItem])

}
