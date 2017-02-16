package org.wex.cmsfs.collecting.impl

import java.util.Date

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import com.typesafe.config.ConfigFactory
import org.quartz.CronExpression
import org.shinhwagk.query.api.{QueryOSMessage, QueryService}
import org.wex.cmsfs.config.api._
import org.wex.cmsfs.format.api.FormatService
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class CollectingAction(pubSub: PubSubRegistry,
                       cs: ConfigService,
                       qs: QueryService,
                       fs: FormatService)(implicit ec: ExecutionContext, mi: Materializer) {

  val jdbcTopic = pubSub.refFor(TopicId[MonitorDetail]("JDBC"))
  val sshTopic = pubSub.refFor(TopicId[MonitorDetail]("SSH"))

  val jdbcSub = jdbcTopic.subscriber
  //  val sshSub =

  loopCollecting

  def loopCollecting = Future {
    while (true) {
      println(System.currentTimeMillis())
      val cDate = new Date() // current Date
      cs.getMonitorDetails
        .invoke()
        .foreach(_.filter(cd => filterCron(cd.cron, cDate))
          .foreach { cd =>
            println(cd)
            cs.getMetricById(cd.metricId)
              .invoke()
              .foreach(_.mode match {
                case "JDBC" => jdbcTopic.publish(cd)
                case _ => sshTopic.publish(cd)
              })
          })
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

  sshTopic.subscriber.mapAsync(10) { md =>
    val a = Random.nextInt()
    val start = System.currentTimeMillis()
    println("收到ssh", a)
    try {
      executeMonitorForSSH(md)
        .map(c => Some(MonitorDepository(None, md.id, c, None, None, System.currentTimeMillis())))
    } catch {
      case ex: Exception => {
        //        throw new Exception(ex.getMessage)
        Future.successful(None)
      }
    }
  }.filter(_.isDefined)
    .map(_.get)
    .mapAsync(10)(md => cs.addMonitorDepository.invoke(md))
    .runWith(Sink.foreach(p=>))

  def filterCron(cron: String, cDate: Date): Boolean = {
    new CronExpression(cron).isSatisfiedBy(cDate)
  }

  def genUrl(stage: String, mode: String, name: String): String = {
    val formatUrl = ConfigFactory.load().getString("format.url")

    stage match {
      case "COLLECT" => formatUrl + "/" + name + "/" + mode.toLowerCase + "/" + "collect.sh"
      case "ANALYZE" => formatUrl + "/" + name + "analyze.py"
    }
  }

  //  def executeMonitor(md: MonitorDetail) = {
  //    for {
  //      metric <- cs.getMetricById(md.metricId).invoke()
  //      collectData <- monitorDistributor(metric, md)
  //    } yield collectData
  //
  //  }
  //
  //  def monitorDistributor(metric: Metric, md: MonitorDetail): Future[String] = {
  //    metric.mode match {
  //      case "SSH" =>
  //        executeMonitorForSSH(md, metric.name)
  //      case "JDBC" =>
  //        Future.successful("")
  //    }
  //  }

  def executeMonitorForSSH(md: MonitorDetail): Future[String] = {
    for {
      metric <- cs.getMetricById(md.metricId).invoke()
      c <- cs.getConnectorSSHById(md.ConnectorId).invoke()
      mh <- cs.getMachineById(c.machineId).invoke()
      collectData <- qs.queryForOSScript
        .invoke(QueryOSMessage(mh.ip, c.user, genUrl("COLLECT", "SSH", metric.name), Some(c.port)))
    } yield collectData
  }

}
