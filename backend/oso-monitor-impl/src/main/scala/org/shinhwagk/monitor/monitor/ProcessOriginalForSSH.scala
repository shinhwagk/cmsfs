package org.shinhwagk.monitor.monitor

import org.shinhwagk.config.api.{ConfigService, MonitorModeJDBC}
import org.shinhwagk.query.api.{QueryOracleMessage, QueryService}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by zhangxu on 2017/1/22.
  */
case class ProcessOriginalForSSH(monitorId: Int, connectorId: Int) {

  var jdbcUrl: String = _
  var username: String = _
  var password: String = _
  var sqlText: String = _

  var result:String = _

  def getConnector(cs: ConfigService) = {
    cs.getMachineConnectorModeJdbc(connectorId).invoke().map(p => {
      jdbcUrl = p.jdbcUrl
      username = p.username
      password = p.password
      this
    })
  }

  def getMonitor(cs: ConfigService) = {
    cs.getMonitorMode("JDBC", monitorId).invoke()
      .map(Json.parse(_).as[MonitorModeJDBC])
      .map(p => {
        sqlText = p.code
        this
      })
  }

  def query(qs: QueryService) = {
    val qom = QueryOracleMessage(jdbcUrl,username,password,sqlText,List("1"))
    qs.queryForOracle("ARRAY").invoke(qom).map(p=>{
      result = p
      this
    })
  }
}