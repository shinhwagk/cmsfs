package org.wex.cmsfs.collect.jdbc.impl

import java.io.{BufferedReader, InputStreamReader}

import akka.stream.scaladsl.Sink
import akka.stream.{ActorAttributes, Materializer, Supervision}
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.monitor.api.{CollectResult, MonitorService}
import play.api.Configuration
import play.api.libs.json.Json

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class Collecting(ct: CollectTopic, ms: MonitorService, config: Configuration)(implicit ec: ExecutionContext, mat: Materializer) {

  private implicit final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private final val source = ct.CollectTopic.subscriber

  source.map(flowLog("debug", "receive jdbc collect", _))
    .mapAsync(10)(x => {
      logger.info("start jdbc collect")
      val c = collectAction(x.host, x.user, genUrl(x.metricName), Some(x.port)).map(rs => (x.id, rs))
      c.onComplete {
        case Success(a) => logger.info(a.toString())
        case Failure(ex) => logger.error(ex.getMessage)
      }
      c
    }).withAttributes(ActorAttributes.supervisionStrategy(decider))
    .mapAsync(10) { case (id, rsOpt) => ms.pushCollectResult.invoke(CollectResult(id, rsOpt)).map(_ => id) }.withAttributes(ActorAttributes.supervisionStrategy(decider))
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
    List(formatUrl, name, "jdbc", "collect.sql").mkString("/")
  }

  def collectAction(host: String, user: String, scriptUrl: String, port: Option[Int] = Some(22)): Future[Option[String]] = Future {
    val DBTYPE = "oracle"
    try {
      if (DBTYPE == "oracle") {
      } else if (DBTYPE == "linux") {
      } else {
        logger.error("OS not match..");
        None
      }
    } catch {
      case ex: Exception => logger.error(ex.getMessage + " collectionAction"); None
    }
  }


}
