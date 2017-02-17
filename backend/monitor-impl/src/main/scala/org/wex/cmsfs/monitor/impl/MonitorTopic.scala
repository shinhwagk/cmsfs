package org.wex.cmsfs.monitor.impl

import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.wex.cmsfs.config.api.MonitorDepository
import org.wex.cmsfs.monitor.api.{MonitorActionDepository, MonitorActionForJDBC, MonitorActionForSSH}

class MonitorTopic(pubSub: PubSubRegistry) {
  val jdbcTopic = pubSub.refFor(TopicId[MonitorActionForJDBC])
  val sshTopic = pubSub.refFor(TopicId[MonitorActionForSSH])
  val monitorDepositoryTopic = pubSub.refFor(TopicId[MonitorActionDepository])


//  jdbcTopic.subscriber.map(md => {
//
//  })
}
