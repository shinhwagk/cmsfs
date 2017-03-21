package org.wex.cmsfs.collect.jdbc.impl

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.{ActorAttributes, Materializer, Supervision}
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.CmsfsAkkaStream
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

  private val supervisionStrategyFun = CmsfsAkkaStream.supervisionStrategy(logger)(_)

  private def loggerFlowFun[T](elem: T, message: String) = CmsfsAkkaStream.loggerFlow(logger)(elem, message)

  source
    .map(elem => loggerFlowFun(elem, s"receive jdbc collect ${elem.connector.id}"))
    .mapAsync(10) { cis =>
      val monitorDetailId = cis.monitorDetailId
      val url = cis.connector.url
      val user = cis.connector.user
      val password = cis.connector.password
      val metricName = cis.collect.name
      val name = cis.connector.name
      val utcDate = cis.utcDate
      val path = cis.collect.path

      collectAction(url, user, password, genUrl(path), Nil)
        .map(rs => (monitorDetailId, metricName, rs, utcDate, name))
    }.withAttributes(supervisionStrategyFun((em) => em + " xx"))
    .mapAsync(10) {
      case (id, metricName, rsOpt, utcDate, name) =>
        ms.pushCollectResult.invoke(CollectResult(id, metricName, rsOpt, utcDate, name)).map(_ => id)
    }.withAttributes(supervisionStrategyFun((em) => em + " xx"))
    .runWith(Sink.foreach(id => logger.info(s"id:${id}, collect success.")))

  def genUrl(path: String): String = {
    val formatUrl = config.getString("collect.url").get
    formatUrl :: Json.parse(path).as[List[String]] mkString "/"
  }

  def collectAction(jdbcUrl: String, user: String, password: String, sqlText: String, parameters: Seq[String]): Future[Option[String]] = {
    val DBTYPE = "oracle"
    try {
      if (DBTYPE == "oracle") {
        val collectOracle = new CollectingOracle(jdbcUrl, user, password, sqlText, parameters)
        collectOracle.mode("MAP").map(Some(_))
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
