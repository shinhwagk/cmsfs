package org.wex.cmsfs.format.alarm.impl

import java.util

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, Method, RequestHeader}
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.common.core.{CmsfsAkkaStream, CmsfsPlayJson, Common}
import org.wex.cmsfs.common.format.FormatCore
import org.wex.cmsfs.common.monitor.MonitorStatus
import org.wex.cmsfs.format.alarm.api.FormatAlarmItem
import org.wex.cmsfs.notification.impl.NotificationService
import play.api.Configuration
import play.api.libs.json.{Format, Json}

import scala.concurrent.Future
import scala.util.Random

class FormatAlarmAction(topic: FormatAlarmTopic,
                        override val config: Configuration,
                        es: NotificationService,
                        system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CmsfsPlayJson with FormatCore with Common with MonitorStatus {

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val subscriber = topic.formatTopic.subscriber

  logger.info(s"${this.getClass.getName} start.")

  def genFormBody(): String = {
    val num = new Random().nextInt(10)
    val nvps2 = new util.ArrayList[NameValuePair]();
    nvps2.add(new BasicNameValuePair("appId", "TOC"));
    nvps2.add(new BasicNameValuePair("orderNo", System.currentTimeMillis().toString));
    nvps2.add(new BasicNameValuePair("protocol", "S"));
    nvps2.add(new BasicNameValuePair("targetIdenty", "13917926210"));
    nvps2.add(new BasicNameValuePair("targetCount", "1"));
    nvps2.add(new BasicNameValuePair("content", s"passw!@#ord +- ${num}"));
    nvps2.add(new BasicNameValuePair("isRealTime", "true"));
    EntityUtils.toString(new UrlEncodedFormEntity(nvps2, "UTF-8"))
  }

  def a(rh: RequestHeader): RequestHeader = {
    rh.withMethod(Method.POST)
      .withProtocol(MessageProtocol(Some("application/x-www-form-urlencoded"), None, None))
      .removeHeader("Accept")
      .addHeader("Accept", "*/*")
  }

  def sendNNN() = {
    es.pushNotificationItem.handleRequestHeader(a).invoke()
  }

  subscriber
    .map(elem => loggerFlow(elem, s"start format alarm ${elem.id}"))
    .mapAsync(10)(actionFormat).withAttributes(supervisionStrategy((x) => x + " xxxx"))
    //    .mapAsync(10) { case (_index, _type, row) => es.(_index, _type).invoke(row) }.withAttributes(supervisionStrategy((x) => x + " xxxx"))
    .runWith(Sink.ignore)

  def actionFormat(fai: FormatAlarmItem): Future[Seq[(String, String)]] = Future {
    val monitorStatus = putStatus(fai.id, "ALARM") _
    try {
      val url: String = getUrlByPath(fai.coreFormatAlarm.path)
      val formatResultString: String = executeFormat(url, "alarm.py", fai.collectResult, fai.coreFormatAlarm.args)
      val formatAlarmResult = Json.parse(formatResultString).as[FormatAlarmResult]
      val mails = fai.coreFormatAlarm.notification.mail.map(mail => (mail, formatAlarmResult.mailResult))
      val phones = fai.coreFormatAlarm.notification.phone.map(phone => (phone, formatAlarmResult.phoneResult))

      mails ++ phones
    } catch {
      case ex: Exception =>
        monitorStatus(false, ex.getMessage)
        throw new Exception(ex.getMessage)
    }
  }
}

case class FormatAlarmResult(mailResult: String, phoneResult: String)

object FormatAlarmResult {
  implicit val format: Format[FormatAlarmResult] = Json.format
}