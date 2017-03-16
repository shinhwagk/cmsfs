package org.wex.cmsfs.collect.jdbc.impl

import akka.stream.scaladsl.Sink
import akka.stream.{ActorAttributes, Materializer, Supervision}
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.monitor.api.{CollectResult, MonitorService}
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class Collecting(ct: CollectTopic, ms: MonitorService, config: Configuration)(implicit ec: ExecutionContext, mat: Materializer) {

  private implicit final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private final val source = ct.CollectTopic.subscriber

  source.map(flowLog("debug", "receive jdbc collect", _))
    .mapAsync(10)(x => {
      logger.info("start jdbc collect")
      val c = collectAction(x.url, x.user, x.password, genUrl(x.metricName), Nil).map(rs => (x.id, x.metricName, rs, x.utcDate, x.name))
      c.onComplete {
        case Success(a) => logger.info(a.toString())
        case Failure(ex) => logger.error(ex.getMessage)
      }
      c
    }).withAttributes(ActorAttributes.supervisionStrategy(decider))
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

  def genUrl(name: String): String = {
    val formatUrl = config.getString("collect.url")
    val url = List(formatUrl, name, "jdbc", "collect.sql").mkString("/")
    logger.info(s"jdbc collect ${url}")
    scala.io.Source.fromURL(url).mkString
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
