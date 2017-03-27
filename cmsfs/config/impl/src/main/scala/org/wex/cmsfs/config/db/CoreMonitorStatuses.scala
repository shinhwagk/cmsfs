package org.wex.cmsfs.config.db

import org.wex.cmsfs.config.api.CoreMonitorStatus
import slick.jdbc.MySQLProfile.api._

class CoreMonitorStatuses(tag: Tag) extends Table[CoreMonitorStatus](tag, "core_monitor_status") {

  def id = column[Option[Int]]("ID", O.PrimaryKey)

  def category = column[String]("CATEGORY")

  def metric = column[String]("METRIC")

  def collectState = column[Boolean]("COLLECT_STATE")

  def collectTimestamp = column[String]("COLLECT_TIMESTAMP")

  def collectError = column[Option[String]]("COLLECT_ERROR")

  def analyzeState = column[Option[Boolean]]("ANALYZE_STATE")

  def analyzeTimestamp = column[Option[String]]("ANALYZE_TIMESTAMP")

  def analyzeError = column[Option[String]]("ANALYZE_ERROR")

  def alarmState = column[Option[Boolean]]("ALARM_STATE")

  def alarmTimestamp = column[Option[String]]("ALARM_TIMESTAMP")

  def alarmError = column[Option[String]]("ALARM_ERROR")

  override def * = (id, category, metric, collectState, collectTimestamp, collectError, analyzeState, analyzeTimestamp, analyzeError, alarmState, alarmTimestamp, alarmError) <> (CoreMonitorStatus.tupled, CoreMonitorStatus.unapply)
}