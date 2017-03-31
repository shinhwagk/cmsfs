package org.wex.cmsfs.collect.jdbc.impl

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.`object`.{CoreCollect, CoreConnectorJdbc}
import org.wex.cmsfs.common.collect.CollectCore
import org.wex.cmsfs.common.core.{CmsfsAkkaStream, Common}
import org.wex.cmsfs.common.monitor.MonitorStatus
import org.wex.cmsfs.format.alarm.api.{FormatAlarmItem, FormatAlarmService}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem, FormatAnalyzeService}
import play.api.Configuration

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Collecting(ct: CollectTopic,
                 analyzeService: FormatAnalyzeService,
                 alarmService: FormatAlarmService,
                 override val config: Configuration,
                 system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CollectCore with Common with MonitorStatus {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val source = ct.CollectTopic.subscriber

  source.map(elem => loggerFlow(elem, s"receive jdbc collect ${elem.connector.id}"))
    .mapAsync(10) { cis =>
      val connector = cis.connector
      val collect = cis.collect

      val collectTimeCalculateFun: (String) => String = collectTimeMonitor

      val statusSave = putStatus(cis.id, "COLLECT") _

      val c = collectFun(connector, collect)
        .map(elem => loggerFlow(elem, collectTimeCalculateFun(collect.name + " " + connector.name)))
        .filter(_.isDefined)
        .map(_.get)
        .flatMap { rs =>
          val sendAnalyze = cis.analyze match {
            case Some(cfa) => analyzeService.pushFormatAnalyze.invoke(FormatAnalyzeItem(cis.id, cis.collect.name, cis.utcDate, rs, cfa))
            case None => Future.successful(Done)
          }
          val sendAlarm = cis.alarm match {
            case Some(cfa) => alarmService.pushFormatAlarm.invoke(FormatAlarmItem(cis.id, rs, cfa))
            case None => Future.successful(Done)
          }
          Future.sequence(sendAnalyze :: sendAlarm :: Nil).map(_ => rs)
        }
      c onComplete {
        case Failure(t) => statusSave(false, t.getMessage)
        case Success(rs) => statusSave(true, rs)
      }
      c
    }.withAttributes(supervisionStrategy((em) => em + " xx"))
    .runWith(Sink.foreach(id => logger.info(s"id:${id}, collect success.")))

  def collectFun(cr: CoreConnectorJdbc, ct: CoreCollect): Future[Option[String]] = {
    val sqlText = getUrlContentByPath(ct.path)
    logger.info(sqlText)
    collectAction(cr.url, cr.user, cr.password, sqlText, Nil)
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