package org.wex.cmsfs.collect.jdbc.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.`object`.{CoreCollect, CoreConnectorJdbc}
import org.wex.cmsfs.common.collect.CollectCore
import org.wex.cmsfs.common.core.{CmsfsAkkaStream, Common}
import org.wex.cmsfs.format.alarm.api.{FormatAlarmItem2, FormatAlarmService}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem2, FormatAnalyzeService}
import play.api.Configuration

import scala.concurrent.Future

class Collecting(ct: CollectTopic,
                 analyzeService: FormatAnalyzeService,
                 alarmService: FormatAlarmService,
                 override val config: Configuration,
                 system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CollectCore with Common {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val source = ct.CollectTopic.subscriber

  source.map(elem => loggerFlow(elem, s"receive jdbc collect ${elem.connector.id}"))
    .mapAsync(10) { cis =>
      val connector = cis.connector
      val collect = cis.collect

      val collectTimeCalculateFun: (String) => String = collectTimeMonitor

      collectFun(connector, collect)
        .map(elem => loggerFlow(elem, collectTimeCalculateFun(collect.name + " " + connector.name)))
        .filter(_.isDefined)
        .map(_.get)
        .flatMap { rs =>
          val sendAnalyze = cis.analyze match {
            case Some(cfa) => analyzeService.pushFormatAnalyze2.invoke(FormatAnalyzeItem2(cis.id, cis.collect.name, cis.utcDate, rs, cfa))
          }
          val sendAlarm = cis.alarm match {
            case Some(cfa) => alarmService.pushFormatAlarm2.invoke(FormatAlarmItem2(cis.id, rs, cfa))
          }
          Future.sequence(sendAnalyze :: sendAlarm :: Nil)
        }
    }.withAttributes(supervisionStrategy((em) => em + " xx"))
    .runWith(Sink.foreach(id => logger.info(s"id:${id}, collect success.")))

  def collectFun(cr: CoreConnectorJdbc, ct: CoreCollect): Future[Option[String]] = {
    val sqlText = getUrlPathContent(ct.path)
    collectAction(cr.url, cr.user, cr.password, sqlText, Nil)
  }

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