package org.wex.cmsfs.collecting.impl

import java.util.Date

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import org.quartz.CronExpression
import org.shinhwagk.query.api.{QueryOSMessage, QueryOracleMessage, QueryService}
import org.wex.cmsfs.config.api.{CollectDetail, ConfigService, DepositoryCollect}
import org.wex.cmsfs.format.api.{FormatItem, FormatService}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

class CollectingAction(pubSub: PubSubRegistry,
                       cs: ConfigService,
                       qs: QueryService,
                       fs: FormatService)(implicit ec: ExecutionContext, materializer: Materializer) {

  val jdbcTopic = pubSub.refFor(TopicId[CollectDetail]("JDBC"))
  val sshTopic = pubSub.refFor(TopicId[CollectDetail]("SSH"))

  val jdbcSub = jdbcTopic.subscriber
  //  val sshSub =

  loopCollecting

  def loopCollecting = Future {
    while (true) {
      println("loop")
      val cDate = new Date() // currten Date
      cs.getCollectDetails
        .invoke()
        .foreach(_.filter(cd => filterCron(cd.cron, cDate))
          .foreach(cd => {
            //            println(cd);
            cd.mode match {
              case "JDBC" => jdbcTopic.publish(cd)
              case _ => sshTopic.publish(cd)
            }
          }))
      Thread.sleep(1000)
    }
  }

  //  jdbcSub.mapAsync(1) { cd =>
  //    val cId = cd.ConnectorId
  //    val mId = cd.monitorId
  //    for {
  //      m <- cs.getMonitorJDBCbyId(mId).invoke()
  //      c <- cs.getConnectorJDBCById(cId).invoke()
  //      q <- qs.queryForOracle("ARRAY")
  //        .invoke(QueryOracleMessage(c.url, c.user, c.password, m.code, cd.args))
  //    } yield DepositoryCollect(None, cd.id, Json.toJson(c).toString(), Json.toJson(m).toString(), q)
  //  }.mapAsync(1)(cs.addDepositoryCollect.invoke _).runWith(Sink.ignore)

  sshTopic.subscriber.mapAsync(4) { cd =>
    println("收到ssh")
    try {
      val cId = cd.ConnectorId
      val mId = cd.monitorId
      for {
        m <- cs.getMonitorSSHById(mId).invoke()
        c <- cs.getConnectorSSHById(cId).invoke()
        mh <- cs.getMachineById(c.machineId).invoke()
        q <- qs.queryForOSScript
          .invoke(QueryOSMessage(mh.ip, c.user, m.code, Some(c.port)))
      } yield {
        println(q)
        Some(DepositoryCollect(None, cd.id, Json.toJson(c).toString(), Json.toJson(m).toString(), q))
      }
    } catch {
      case ex: Exception => {
        println(ex.getMessage)
        Future.successful(None)
      }
    }
  }.async
    .filter(_.isDefined)
    .map(_.get)
    .mapAsync(8) { dc =>
      for {
        n <- cs.addDepositoryCollect.invoke(dc)
        p <- fs.pushFormat.invoke(FormatItem(dc.data, 1))
      } yield None
    }.async
    .runWith(Sink.ignore)

  def filterCron(cron: String, cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }
}
