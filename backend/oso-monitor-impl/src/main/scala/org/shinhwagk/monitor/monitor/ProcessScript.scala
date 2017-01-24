package org.shinhwagk.monitor.monitor

import org.shinhwagk.config.api.{ConfigService, MonitorDetail, MonitorPersistence}
import org.shinhwagk.query.api.QueryService
import play.api.libs.json.JsValue

import scala.concurrent.Future

/**
  * Created by shinhwagk on 2017/1/22.
  */
trait ProcessScript {

  def getScriptResult: Future[JsValue]
}