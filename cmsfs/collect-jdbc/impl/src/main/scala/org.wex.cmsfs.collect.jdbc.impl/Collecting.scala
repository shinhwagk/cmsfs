package org.wex.cmsfs.collect.jdbc.impl

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.{ActorAttributes, Materializer, Supervision}
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.monitor.api.{CollectResult, MonitorService}
import play.api.Configuration
import play.api.libs.json.Json
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Collecting(ct: CollectTopic,
                 ms: MonitorService,
                 config: Configuration,
                 system: ActorSystem)(implicit mat: Materializer) {

  private implicit val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val source = ct.CollectTopic.subscriber

  source.map(flowLog("debug", "receive jdbc collect", _))
    .mapAsync(10) { cis =>
      logger.info("start jdbc collect")

      val monitorDetailId = cis.monitorDetailId
      val url = cis.connector.url
      val user = cis.connector.user
      val password = cis.connector.password
      val metricName = cis.collect.name
      val name = cis.connector.name
      val utcDate = cis.utcDate
      val path = cis.collect.path

      val c = collectAction(url, user, password, genUrl(path, metricName), Nil).map(rs => (monitorDetailId, metricName, rs, utcDate, name))
      c.onComplete {
        case Success(a) => logger.info(a.toString())
        case Failure(ex) => logger.error(ex.getMessage)
      }
      c
    }.withAttributes(ActorAttributes.supervisionStrategy(decider))
    .mapAsync(10) {
      case (id, metricName, rsOpt, utcDate, name) =>
        ms.pushCollectResult.invoke(CollectResult(id, metricName, rsOpt, utcDate, name)).map(_ => id)
    }.withAttributes(ActorAttributes.supervisionStrategy(decider))
    .runWith(Sink.foreach(id => logger.info(s"id:${id}, collect success.")))

  def flowLog[T](level: String, log: String, elem: T): T = {
    logger.info(log)
    elem
  }

  def decider(implicit log: Logger): Supervision.Decider = {
    case ex: Exception =>
      log.error(ex.getMessage + " XXXX")
      Supervision.Resume
  }

  def genUrl(path: String, name: String): String = {
    val formatUrl = config.getString("collect.url").get
    formatUrl :: Json.parse(path).as[Seq[String]] :: "collect.sql" :: Nil mkString "/"
  }

  def collectAction(jdbcUrl: String, user: String, password: String, sqlText: String, parameters: Seq[String]): Future[Option[String]] = {
    val DBTYPE = "oracle"
    try {
      if (DBTYPE == "oracle") {
        new CollectingOracle(jdbcUrl, user, password, sqlText, parameters).mode("MAP").map(Some(_))
      } else if (DBTYPE == "mysql") {
        Future.successful(None)
      } else {
        logger.error("DBTYPE not match..");
        Future.successful(None)
      }
    } catch {
      case ex: Exception => logger.error(ex.getMessage + " collectionAction"); Future.successful(None)
    }
  }


}
