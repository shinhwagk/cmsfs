package org.shinhwagk.alarm.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import play.api.libs.json.JsValue

trait AlarmService extends Service {

  def createAlarm: ServiceCall[Alarm, Alarm]

  def getAlarmResult(id: Int): ServiceCall[List[String], JsValue]

  override final def descriptor = {
    named("oso-monitor-alarm").withCalls(
      restCall(Method.POST, "/v1/monitor/alarm/:id", getAlarmResult _),
      restCall(Method.POST, "/v1/alarm", createAlarm)
    )
  }
}
