package org.wex.cmsfs.collecting.impl

import java.util.Date

import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.quartz.CronExpression
import org.shinhwagk.config.api.{CollectDetail, ConfigService}
import org.shinhwagk.query.api.{QueryOSMessage, QueryOracleMessage, QueryService}

import scala.concurrent.{ExecutionContext, Future}

class CollectingAction(pubSub: PubSubRegistry,
                       persistentEntityRegistry: PersistentEntityRegistry,
                       cs: ConfigService,
                       qs: QueryService)(implicit ec: ExecutionContext) {

  val jdbcTopic = pubSub.refFor(TopicId[CollectDetail])
  val sshTopic = pubSub.refFor(TopicId[CollectDetail])

  val jdbcSub = jdbcTopic.subscriber
  val sshSub = sshTopic.subscriber

  loopCollecting

  def loopCollecting = Future {
    while (true) {
      val cDate = new Date() // currten Date
      cs.getCollectDetails
        .invoke()
        .map(_.filter(cd => filterCron(cd.cron, cDate)))
        .map(_.foreach(cd =>
          cd.mode match {
            case "JDBC" => jdbcTopic.publish(cd)
            case _ => sshTopic.publish(cd)
          }))
      Thread.sleep(1000)
    }
  }

  jdbcSub.mapAsync(1) { cd =>
    val cId = cd.ConnectorId
    val mId = cd.monitorId
    for {
      m <- cs.getMonitorJDBCbyId(mId).invoke()
      c <- cs.getConnectorJDBCById(cId).invoke()
      q <- qs.queryForOracle("ARRAY")
        .invoke(QueryOracleMessage(c.url, c.user, c.password, m.code, cd.args))
    } yield q
  }.runForeach(println)

  sshSub.mapAsync(1) { cd =>
    val cId = cd.ConnectorId
    val mId = cd.monitorId
    for {
      m <- cs.getMonitorSSHById(mId).invoke()
      c <- cs.getConnectorSSHById(cId).invoke()
      q <- qs.queryForOSScript
        .invoke(QueryOSMessage("a", c.user, m.code, Some(c.port)))
    } yield q
  }.runForeach(println)

  def filterCron(cron: String, cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }
}
