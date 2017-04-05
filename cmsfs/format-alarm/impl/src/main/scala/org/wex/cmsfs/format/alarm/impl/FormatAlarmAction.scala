package org.wex.cmsfs.format.alarm.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, RequestHeader}
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.core.{CmsfsAkkaStream, CmsfsPlayJson, Common}
import org.wex.cmsfs.common.format.FormatCore
import org.wex.cmsfs.format.alarm.api.FormatAlarmItem
import org.wex.cmsfs.notification.impl.NotificationService
import play.api.Configuration
import play.api.libs.json.{JsArray, JsValue, Json}
import scala.concurrent.Future

class FormatAlarmAction(topic: FormatAlarmTopic,
                        override val config: Configuration,
                        es: NotificationService,
                        system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CmsfsPlayJson with FormatCore with Common {

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val subscriber = topic.formatTopic.subscriber

  logger.info(s"${this.getClass.getName} start.")

  def a(rh: RequestHeader): RequestHeader = {
    rh.withProtocol(MessageProtocol(Some("application/x-www-form-urlencoded")))
  }

  es.pushNotificationItem.handleRequestHeader(a).invoke()

  subscriber
  //    .map(elem => loggerFlow(elem, s"start format alarm ${elem.id}"))
  //    .mapAsync(10)(actionFormat).withAttributes(supervisionStrategy((x) => x + " xxxx"))
  //    .map(elem => loggerFlow(elem, s"send format alarm ${elem._metric}"))
  //    .mapConcat(fai => splitAnalyzeResult(fai).toList)
  //    .mapAsync(10) {}
  //    //    .mapAsync(10) { case (_index, _type, row) => es.(_index, _type).invoke(row) }.withAttributes(supervisionStrategy((x) => x + " xxxx"))
  //    .runWith(Sink.ignore)
  //
  //  def splitAnalyzeResult(fai: FormatAlarmItem): Seq[(String, String, String)] = {
  //    try {
  //      val formatResult: String = fai.formatResult.get
  //      val _type = fai._type
  //      val _index = fai._index
  //      val _metric = fai._metric
  //      val utcDate = fai.utcDate
  //      val arr: Seq[JsValue] = Json.parse(formatResult).as[JsArray].value
  //      arr.map(jsonObjectAddField(_, "@timestamp", utcDate))
  //        .map(jsonObjectAddField(_, "@metric", _metric))
  //        .map(row => (_index, _type, row.toString))
  //    } catch {
  //      case ex: Exception => {
  //        logger.error("splitAnalyzeResult " + ex.getMessage)
  //        Seq()
  //      }
  //    }
  //  }
  //
  //  def actionFormat(fai: FormatAlarmItem): Future[FormatAlarmItem] = Future {
  //    val url: String = getUrlPathContent(fai.path)
  //    val formatResult = executeFormat(url, "Alarm.py", fai.collectResult, fai.args)
  //    executeFormatAfter(workDirName)
  //    fai.copy(formatResult = Some(rs))
  //  }
}
