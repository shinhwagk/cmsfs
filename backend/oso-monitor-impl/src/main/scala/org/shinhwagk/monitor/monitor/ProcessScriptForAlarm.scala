package org.shinhwagk.monitor.monitor

import java.io.{File, PrintWriter}

import com.lightbend.lagom.scaladsl.api.Service
import org.shinhwagk.config.api.ConfigService
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future
import sys.process._

/**
  * Created by zhangxu on 2017/1/24.
  */
case class ProcessScriptForAlarm(id: Int, cs: ConfigService) extends ProcessScript {
  override def getScriptResult: Future[JsValue] = {
    cs.getAlarm(id).invoke().map(alarm => {
      val pw = new PrintWriter(new File("hello.ps1"))
      pw.write(alarm.script)
      pw.close
      alarm
    }).map(alarm => s"""powershell -File hello.ps1 ${alarm.args}""".!!).map(Json.parse _)
  }
}
