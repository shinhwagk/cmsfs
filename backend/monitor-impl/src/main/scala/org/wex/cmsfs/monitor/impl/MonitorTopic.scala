package org.wex.cmsfs.monitor.impl

import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.monitor.api.{MonitorActionDepository, MonitorActionForJDBC, MonitorActionForSSH}

import scala.concurrent.ExecutionContext

class MonitorTopic(pubSub: PubSubRegistry)(implicit ec: ExecutionContext, mi: Materializer) {

  val jdbcCollectTopic = pubSub.refFor(TopicId[MonitorActionForJDBC])

  val sshCollectTopic = pubSub.refFor(TopicId[MonitorActionForSSH])

  val monitorDepositoryTopic = pubSub.refFor(TopicId[MonitorActionDepository])

}
