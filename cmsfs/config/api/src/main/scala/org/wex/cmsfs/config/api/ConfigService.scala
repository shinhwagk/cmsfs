package org.wex.cmsfs.config.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait ConfigService extends Service {

  def getCoreMonitorDetails: ServiceCall[NotUsed, Seq[CoreMonitorDetail]]

  def getCoreMonitorDetailById(id: Int): ServiceCall[NotUsed, CoreMonitorDetail]

  def addCoreMonitorDetail: ServiceCall[CoreMonitorDetail, Done]

  def getCoreConnectorJdbcById(id: Int): ServiceCall[NotUsed, CoreConnectorJdbc]

  def addCoreConnectorJdbc: ServiceCall[CoreConnectorJdbc, Done]

  def getCoreConnectorSshById(id: Int): ServiceCall[NotUsed, CoreConnectorSsh]

  def addCoreConnectorSsh: ServiceCall[CoreConnectorSsh, Done]

  def getCoreCollectById(id: Int): ServiceCall[NotUsed, CoreCollect]

  def addCoreCollect: ServiceCall[CoreCollect, Done]

  def getCoreFormatAnalyzesById(id: Int): ServiceCall[NotUsed, CoreFormatAnalyze]

  def addCoreFormatAnalyze: ServiceCall[CoreFormatAnalyze, Done]

  def getCoreFormatAlarmsById(id: Int): ServiceCall[NotUsed, CoreFormatAlarm]

  def addCoreFormatAlarm: ServiceCall[CoreFormatAlarm, Done]

  def addCoreMonitorStatus: ServiceCall[CoreMonitorStatus, Done]

  def putCoreMonitorStatusCollectById(id: Int): ServiceCall[List[String], Done]

  def putCoreMonitorStatusAnalyzeById(id: Int): ServiceCall[List[String], Done]

  def putCoreMonitorStatusAlarmById(id: Int): ServiceCall[List[String], Done]

  override final def descriptor = {
    named("config").withCalls(
      restCall(Method.GET, "/v1/core/monitor/details", getCoreMonitorDetails),
      restCall(Method.GET, "/v1/core/monitor/detail/:id", getCoreMonitorDetailById _),
      restCall(Method.POST, "/v1/core/monitor/detail", addCoreMonitorDetail),
      restCall(Method.GET, "/v1/core/connector/jdbc/:id", getCoreConnectorJdbcById _),
      restCall(Method.POST, "/v1/core/connector/jdbc", addCoreConnectorJdbc),
      restCall(Method.GET, "/v1/core/connector/ssh/:id", getCoreConnectorSshById _),
      restCall(Method.POST, "/v1/core/connector/ssh", addCoreConnectorSsh),
      restCall(Method.GET, "/v1/core/collect/:id", getCoreCollectById _),
      restCall(Method.POST, "/v1/core/collect", addCoreCollect),
      restCall(Method.GET, "/v1/core/format/analyze/:id", getCoreFormatAnalyzesById _),
      restCall(Method.POST, "/v1/core/format/analyze", addCoreFormatAnalyze),
      restCall(Method.GET, "/v1/core/format/alarm/:id", getCoreFormatAlarmsById _),
      restCall(Method.POST, "/v1/core/format/alarm", addCoreFormatAlarm),
      //      ,
      restCall(Method.POST, "/v1/core/monitor/status", addCoreMonitorStatus),
      restCall(Method.PUT, "/v1/core/monitor/status/collect/:id", putCoreMonitorStatusCollectById _),
      restCall(Method.PUT, "/v1/core/monitor/status/analyze/:id", putCoreMonitorStatusAnalyzeById _),
      restCall(Method.PUT, "/v1/core/monitor/status/alarm/:id", putCoreMonitorStatusAnalyzeById _)
    )
  }
}