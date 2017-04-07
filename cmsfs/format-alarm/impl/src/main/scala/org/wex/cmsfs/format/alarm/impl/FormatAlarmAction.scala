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
import scala.util.{Failure, Success}

class FormatAlarmAction(topic: FormatAlarmTopic,
                        override val config: Configuration,
                        es: NotificationService,
                        system: ActorSystem)(implicit mat: Materializer)
  extends CmsfsAkkaStream with CmsfsPlayJson with FormatCore with Common with MonitorStatus {

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  logger.info(s"${this.getClass.getName} start.3")

  def genFormBody(contact: Seq[String], content: String): Unit = {
    val nvps2 = new util.ArrayList[NameValuePair]();
    nvps2.add(new BasicNameValuePair("appId", "TOC"));
    nvps2.add(new BasicNameValuePair("orderNo", System.currentTimeMillis().toString));
    nvps2.add(new BasicNameValuePair("protocol", "S"));
    nvps2.add(new BasicNameValuePair("targetIdenty", contact.mkString(",")));
    nvps2.add(new BasicNameValuePair("targetCount", "1"));
    nvps2.add(new BasicNameValuePair("content", content));
    nvps2.add(new BasicNameValuePair("isRealTime", "true"));
    val body = EntityUtils.toString(new UrlEncodedFormEntity(nvps2, "UTF-8"))
    es.pushNotificationItem.handleRequestHeader(setHeader).invoke(body)
  }

  def genFormBody(subject: String, contact: Seq[String], content: String): Unit = {
    val nvps2 = new util.ArrayList[NameValuePair]();
    nvps2.add(new BasicNameValuePair("appId", "TOC"));
    nvps2.add(new BasicNameValuePair("orderNo", System.currentTimeMillis().toString));
    nvps2.add(new BasicNameValuePair("protocol", "m"));
    nvps2.add(new BasicNameValuePair("targetIdenty", contact.mkString(",")));
    nvps2.add(new BasicNameValuePair("targetCount", "1"));
    nvps2.add(new BasicNameValuePair("content", content));
    nvps2.add(new BasicNameValuePair("isRealTime", "true"));
    nvps2.add(new BasicNameValuePair("subject", subject));
    val body = EntityUtils.toString(new UrlEncodedFormEntity(nvps2, "UTF-8"))
    es.pushNotificationItem.handleRequestHeader(setHeader).invoke(body).onComplete {
      case Success(s) => logger.info(s)
      case Failure(ex) => logger.info(ex.getMessage)
    }
  }

  def setHeader(rh: RequestHeader): RequestHeader = {
    rh.withMethod(Method.POST)
      .withProtocol(MessageProtocol(Some("application/x-www-form-urlencoded"), None, None))
      .removeHeader("Accept")
      .addHeader("Accept", "*/*")
  }

  def actionFormat(fai: FormatAlarmItem) = Future {
    val monitorStatus = putStatus(fai.id, "ALARM") _
    try {
      val url: String = getUrlByPath(fai.coreFormatAlarm.path)
      val formatResultString: String = executeFormat(url, "alarm.py", fai.collectResult, fai.coreFormatAlarm.args)
      val formatAlarmResult: FormatAlarmResult = Json.parse(formatResultString).as[FormatAlarmResult]
      val mails = fai.coreFormatAlarm.notification.mails.map(mail => (mail, formatAlarmResult.mailResult))
      val phones = fai.coreFormatAlarm.notification.mobiles.map(phone => (phone, formatAlarmResult.phoneResult))

      logger.info(s"${fai.coreFormatAlarm.notification.mails} ${formatAlarmResult.mailResult}")

      genFormBody("test", fai.coreFormatAlarm.notification.mails, formatAlarmResult.mailResult)
    } catch {
      case ex: Exception =>
        monitorStatus(false, ex.getMessage)
        throw new Exception(ex.getMessage)
    }
  }

  topic.formatTopic.subscriber
    .map(elem => loggerFlow(elem, s"start format alarm ${elem.id}"))
    .mapAsync(10)(actionFormat).withAttributes(supervisionStrategy((x) => x + " xxxx"))
    .runWith(Sink.ignore)
}

case class FormatAlarmResult(mailResult: String, phoneResult: String)

object FormatAlarmResult {
  implicit val format: Format[FormatAlarmResult] = Json.format
}