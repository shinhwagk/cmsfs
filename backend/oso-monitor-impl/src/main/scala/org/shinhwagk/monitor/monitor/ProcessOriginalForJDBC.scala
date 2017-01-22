package org.shinhwagk.monitor.monitor

import org.shinhwagk.config.api.{ConfigService, MonitorDetail, MonitorModeJDBC, MonitorPersistence}
import org.shinhwagk.query.api.{QueryOracleMessage, QueryService}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by zhangxu on 2017/1/22.
  */

trait ProcessOriginal {
  def genPersistence(version: Long): MonitorPersistence

  def getConnector(cs: ConfigService):Future[ProcessOriginal]

  def getMonitor(cs: ConfigService):Future[ProcessOriginal]

  def query(qs: QueryService):Future[ProcessOriginal]
}

case class ProcessOriginalForJDBC(md: MonitorDetail) extends ProcessOriginal {

  var jdbcUrl: String = _
  var username: String = _
  var password: String = _
  var sqlText: String = _

  var result: String = _

  override def getConnector(cs: ConfigService) = {
    cs.getMachineConnectorModeJdbc(md.machineConnectorId).invoke().map(p => {
      jdbcUrl = p.jdbcUrl
      username = p.username
      password = p.password
      this
    })
  }

  override def getMonitor(cs: ConfigService) = {
    cs.getMonitorMode("JDBC", md.monitorModeId).invoke()
      .map(Json.parse(_).as[MonitorModeJDBC])
      .map(p => {
        sqlText = p.code
        this
      })
  }

  override def query(qs: QueryService) = {
    val qom = QueryOracleMessage(jdbcUrl, username, password, sqlText, List("1"))
    qs.queryForOracle("ARRAY").invoke(qom).map(p => {
      result = p
      this
    })
  }

  override def genPersistence(version: Long): MonitorPersistence = {
    MonitorPersistence(None, "original", version, result, md.id.get)
  }
}