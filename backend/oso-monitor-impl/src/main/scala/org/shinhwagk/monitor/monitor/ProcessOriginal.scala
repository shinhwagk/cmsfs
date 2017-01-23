package org.shinhwagk.monitor.monitor

import org.shinhwagk.config.api.{ConfigService, MonitorPersistence}
import org.shinhwagk.query.api.QueryService

import scala.concurrent.Future

/**
  * Created by shinhwagk on 2017/1/22.
  */
trait ProcessOriginal {
  def genPersistence(version: Long): MonitorPersistence

  def getConnector(cs: ConfigService):Future[ProcessOriginal]

  def getMonitor(cs: ConfigService):Future[ProcessOriginal]

  def query(qs: QueryService):Future[ProcessOriginal]
}