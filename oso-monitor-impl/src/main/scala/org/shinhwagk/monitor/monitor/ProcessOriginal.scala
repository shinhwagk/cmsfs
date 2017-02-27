package org.shinhwagk.monitor.monitor

import org.shinhwagk.config.api.{ConfigService, MonitorDetail, MonitorPersistence}
import org.shinhwagk.query.api.QueryService

import scala.concurrent.Future

/**
  * Created by shinhwagk on 2017/1/22.
  */
trait ProcessOriginal {
  val version: Long

  val md: MonitorDetail

  val cs: ConfigService

  val qs: QueryService

  var result: String

  def getConnector: Future[ProcessOriginal]

  def getMonitor: Future[ProcessOriginal]

  def query: Future[ProcessOriginal]

  def genPersistence: Future[ProcessOriginal] =
    cs.addMonitorPersistence.invoke(MonitorPersistence(None, "original", version, result, md.id.get))
      .map(_ => this)

  lazy val ProcessAlarm: Option[ProcessScript] =
    if (md.alarm >= 1) Some(ProcessScriptForAlarm(md.alarm, cs)) else None

  lazy val ProcessReport: Option[ProcessScript] =
    if (md.report >= 1) Some(ProcessScriptForReport(md.report, cs)) else None

  lazy val ProcessChart: Option[ProcessScript] =
    if (md.chart >= 1) Some(ProcessScriptForChart(md.alarm, cs)) else None
}