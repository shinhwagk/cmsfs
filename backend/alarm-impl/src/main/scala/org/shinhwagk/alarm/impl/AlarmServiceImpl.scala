package org.shinhwagk.alarm.impl

import java.io.{File, PrintWriter}

import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.alarm.api.AlarmService
import org.shinhwagk.config.api.ConfigService
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._

/**
  * Implementation of the LagomhelloService.
  */
class AlarmServiceImpl(configService: ConfigService)(implicit ec: ExecutionContext) extends AlarmService {
  override def getAlarmResult(id: Int): ServiceCall[List[String], JsValue] = ServiceCall { _ =>
    configService.getAlarm(id).invoke().map(alarm => {
      println(alarm)
      val pw = new PrintWriter(new File("hello.ps1"))
      pw.write(alarm.script)
      pw.close
    }).map(_ => s"powershell -File hello.ps1".!!).map(Json.parse _)
  }
}