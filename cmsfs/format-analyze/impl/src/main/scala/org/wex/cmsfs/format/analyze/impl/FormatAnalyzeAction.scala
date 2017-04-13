package org.wex.cmsfs.format.analyze.impl

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.core.CmsfsAkkaStream
import org.wex.cmsfs.common.format.FormatCore
import org.wex.cmsfs.common.monitor.MonitorStatus
import org.wex.cmsfs.elasticsearch.api.ElasticsearchService
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeItem
import play.api.Configuration
import play.api.libs.json._

import scala.concurrent.Future

class FormatAnalyzeAction(topic: FormatAnalyzeTopic,
                          override val config: Configuration,
                          es: ElasticsearchService,
                          system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with FormatAnalyzeCore with FormatCore with MonitorStatus {

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val subscriber = topic.formatTopic.subscriber

  logger.info(s"${this.getClass.getName} start.")

  var count = 0

  subscriber
    .map(elem => loggerFlow(elem, {
      count += 1;
      s"add ${count}"
    }))
    .mapAsync(10)(newAnalyzeResultFormat)
    .map(elem => loggerFlow(elem, {
      count -= 1;
      s"sub ${count}"
    }))
    .runWith(Sink.foreach(_ => logger.info("format analyze success.")))

  //  subscriber
  //    .map(elem => loggerFlow(elem, s"start format analyze ${elem.id}"))
  //    .mapAsync(10)(analyzeResultFormat).withAttributes(supervisionStrategy((x) => x + " xxxxy"))
  //    .map(elem => loggerFlow(elem, s"end analyzeResultFormat."))
  //    .mapConcat(p => p.toList)
  //    .mapAsync(10) { case List(_index, _type, row) => es.pushElasticsearchItem(_index, _type).invoke(row) }
  //    .withAttributes(supervisionStrategy((x) => x + " xxxx"))
  //    .runWith(Sink.foreach(_ => logger.info("format analyze success.")))

  //  def analyzeResultFormat(fai: FormatAnalyzeItem): Future[Seq[List[String]]] = Future {
  //    val monitorStatus = putStatus(fai.id, "ANALYZE") _
  //    try {
  //      val formatResult = executeFormat(fai.id, fai.coreFormatAnalyze.path, "analyze.py", fai.collectResult, fai.coreFormatAnalyze.args.getOrElse(""))
  //      val _type = fai.coreFormatAnalyze.elasticsearch._type
  //      val _index = fai.coreFormatAnalyze.elasticsearch._index
  //      val _metric = fai._metric
  //      val utcDate = fai.utcDate
  //      val arr: Seq[JsValue] = Json.parse(formatResult).as[JsArray].value
  //      val rs: Seq[List[String]] = arr.map(jsonObjectAddField(_, "@timestamp", utcDate))
  //        .map(jsonObjectAddField(_, "@metric", _metric))
  //        .map(row => List(_index, _type, row.toString))
  //      monitorStatus(true, rs.toString())
  //      rs
  //    } catch {
  //      case ex: Exception =>
  //        monitorStatus(false, ex.getMessage)
  //        throw new Exception(ex.getMessage)
  //    }
  //  }

  def newAnalyzeResultFormat(fai: FormatAnalyzeItem): Future[Seq[Done]] = {
    try {
      val _type = fai.coreFormatAnalyze.elasticsearch._type
      val _index = fai.coreFormatAnalyze.elasticsearch._index
      val _metric = fai._metric
      val utcDate = fai.utcDate

      logger.info(s"format analyze. ${_metric} - ${_type} ${utcDate}")
      val formatResult = executeFormat(fai.id, fai.coreFormatAnalyze.path, "analyze.py", fai.collectResult, fai.coreFormatAnalyze.args.getOrElse(""))

      val arr: Seq[JsValue] = Json.parse(formatResult).as[JsArray].value

      val rs: Seq[String] = formatResultAddFiled(arr, utcDate, _metric)

      Future.sequence(rs.map(row => es.pushElasticsearchItem(_index, _type).invoke(row))).map { p =>
        logger.info(s"send es. ${_metric} - ${_type} ${utcDate}")
        p
      }
    } catch {
      case ex: Exception =>
        throw new Exception(ex.getMessage)
    }
  }

}