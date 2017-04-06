package org.wex.cmsfs.format.alarm.impl

import java.util

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, Method, RequestHeader}
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.core.{CmsfsAkkaStream, CmsfsPlayJson, Common}
import org.wex.cmsfs.common.format.FormatCore
import org.wex.cmsfs.notification.impl.NotificationService
import play.api.Configuration

import scala.util.{Failure, Success}

class FormatAlarmAction(topic: FormatAlarmTopic,
                        override val config: Configuration,
                        es: NotificationService,
                        system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CmsfsPlayJson with FormatCore with Common {

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val subscriber = topic.formatTopic.subscriber

  logger.info(s"${this.getClass.getName} start.")

  def genFormBody: String = {
    val nvps2 = new util.ArrayList[NameValuePair]();
    nvps2.add(new BasicNameValuePair("appId", "TOC"));
    nvps2.add(new BasicNameValuePair("orderNo", System.currentTimeMillis().toString));
    nvps2.add(new BasicNameValuePair("protocol", "S"));
    nvps2.add(new BasicNameValuePair("targetIdenty", "13917926210"));
    nvps2.add(new BasicNameValuePair("targetCount", "1"));
    nvps2.add(new BasicNameValuePair("content", "passw!@#$*&^%ord +-09"));
    nvps2.add(new BasicNameValuePair("isRealTime", "true"));
    EntityUtils.toString(new UrlEncodedFormEntity(nvps2, "UTF-8"))
  }

  def a(rh: RequestHeader): RequestHeader = {
    val c = rh.withMethod(Method.POST)
      .withProtocol(MessageProtocol(Some("application/x-www-form-urlencoded")))

    logger.info(c.protocol.toString)
    logger.info(c.method.name)
    logger.info(c.principal.toString)
    logger.info(c.uri.toString)
    c
  }

  logger.info(genFormBody)

  val request = es.pushNotificationItem.handleRequestHeader(a)

  request.invoke(genFormBody).onComplete {
    case Success(a) => println("success " + a)
    case Failure(ex) => println("failure " + ex.getMessage)
  }

  //  subscriber
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


//    nvps2.add(new BasicNameValuePair("appId", "TOC"));
//    nvps2.add(new BasicNameValuePair("orderNo", System.currentTimeMillis().toString));
//    nvps2.add(new BasicNameValuePair("protocol", "M"));
//    nvps2.add(new BasicNameValuePair("targetIdenty", "zhangxu@weibopay.com"));
//    nvps2.add(new BasicNameValuePair("targetCount", "1"));
//    nvps2.add(new BasicNameValuePair("content", "passw!@#$*&^%ord +-09"));
//    nvps2.add(new BasicNameValuePair("isRealTime", "true"));
//    nvps2.add(new BasicNameValuePair("subject", "JIRA (WCF-3549) FCM迭代"));