package org.wex.cmsfs.collect.jdbc.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.{CmsfsAkkaStream, Common}
import org.wex.cmsfs.common.collect.CollectCore
import org.wex.cmsfs.monitor.api.{CollectResult, MonitorService}
import play.api.Configuration

import scala.concurrent.Future

class Collecting(ct: CollectTopic,
                 ms: MonitorService,
                 override val config: Configuration,
                 system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CollectCore with Common{

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val source = ct.CollectTopic.subscriber

  source.map(elem => loggerFlow(elem, s"receive jdbc collect ${elem.connector.id}"))
    .mapAsync(10) { cis =>

      val monitorDetailId = cis.monitorDetailId
      val url = cis.connector.url
      val user = cis.connector.user
      val password = cis.connector.password
      val metricName = cis.collect.name
      val dbName = cis.connector.name
      val utcDate = cis.utcDate
      val path = cis.collect.path

      val sqlText = getUrlPathContent(path)

      val collectTimeCalculateFun: (String) => String = collectTimeMonitor

      collectAction(url, user, password, sqlText, Nil)
        .map(elem => loggerFlow(elem, collectTimeCalculateFun(metricName + " " + dbName)))
        .filter(_.isDefined)
        .map(rs => (monitorDetailId, metricName, rs, utcDate, dbName))
    }.withAttributes(supervisionStrategy((em) => em + " xx"))
    .mapAsync(10) {
      case (id, metricName, rsOpt, utcDate, name) =>
        ms.pushCollectResult.invoke(CollectResult(id, metricName, rsOpt, utcDate, name)).map(_ => id)
    }.withAttributes(supervisionStrategy((em) => em + " xx"))
    .runWith(Sink.foreach(id => logger.info(s"id:${id}, collect success.")))

  def collectAction(jdbcUrl: String, user: String, password: String, sqlText: String, parameters: Seq[String]): Future[Option[String]] = {
    val DBTYPE = "oracle"
    try {
      if (DBTYPE == "oracle") {
        val collectOracle = new CollectingOracle(jdbcUrl, user, password, sqlText, parameters)
        val c = collectOracle.mode("MAP").map(Some(_))
        c.foreach(rs => println("xxx " + rs + " xxxxxxx"))
        c
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