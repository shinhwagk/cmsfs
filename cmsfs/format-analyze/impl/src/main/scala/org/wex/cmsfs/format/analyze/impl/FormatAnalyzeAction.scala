package org.wex.cmsfs.format.analyze.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.core.{CmsfsAkkaStream, CmsfsPlayJson, Common}
import org.wex.cmsfs.common.format.FormatCore
import org.wex.cmsfs.elasticsearch.api.ElasticsearchService
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeItem
import play.api.Configuration
import play.api.libs.json._

import scala.concurrent.Future

class FormatAnalyzeAction(topic: FormatAnalyzeTopic,
                          override val config: Configuration,
                          es: ElasticsearchService,
                          system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CmsfsPlayJson with FormatCore with Common {

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val subscriber = topic.formatTopic.subscriber

  logger.info(s"${this.getClass.getName} start.")

  subscriber
    .map(elem => loggerFlow(elem, s"start format analyze ${elem.id}"))
    .mapAsync(10)(actionFormat).withAttributes(supervisionStrategy((x) => x + " xxxx"))
    .mapConcat(p => p.toList)
    .mapAsync(10) { case (_index, _type, row) => es.pushElasticsearchItem(_index, _type).invoke(row) }.withAttributes(supervisionStrategy((x) => x + " xxxx"))
    .runWith(Sink.ignore)

  def splitAnalyzeResult(fai: FormatAnalyzeItem): Seq[(String, String, String)] = {
    try {
      val formatResult: String = fai.collectResult
      val _type = fai.coreFormatAnalyze.elasticsearch._type
      val _index = fai.coreFormatAnalyze.elasticsearch._index
      val _metric = fai._metric
      val utcDate = fai.utcDate
      val arr: Seq[JsValue] = Json.parse(formatResult).as[JsArray].value
      arr.map(jsonObjectAddField(_, "@timestamp", utcDate))
        .map(jsonObjectAddField(_, "@metric", _metric))
        .map(row => (_index, _type, row.toString))
    } catch {
      case ex: Exception => {
        logger.error("splitAnalyzeResult " + ex.getMessage)
        Seq()
      }
    }
  }

  def actionFormat(fai: FormatAnalyzeItem): Future[Seq[(String, String, String)]] = Future {
    val url: String = getUrlContentByPath(fai.coreFormatAnalyze.path)
    val formatResult = executeFormat(url, "analyze.py", fai.collectResult, fai.coreFormatAnalyze.args.get)
    val _type = fai.coreFormatAnalyze.elasticsearch._type
    val _index = fai.coreFormatAnalyze.elasticsearch._index
    val _metric = fai._metric
    val utcDate = fai.utcDate
    val arr: Seq[JsValue] = Json.parse(formatResult).as[JsArray].value
    arr.map(jsonObjectAddField(_, "@timestamp", utcDate))
      .map(jsonObjectAddField(_, "@metric", _metric))
      .map(row => (_index, _type, row.toString))
  }
}