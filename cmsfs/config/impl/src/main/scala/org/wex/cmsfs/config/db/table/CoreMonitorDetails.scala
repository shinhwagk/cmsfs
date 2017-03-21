package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.CoreMonitorDetail
import slick.jdbc.MySQLProfile.api._

class CoreMonitorDetails(tag: Tag) extends Table[CoreMonitorDetail](tag, "core_monitor_detail") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def cron = column[String]("CRON")

  def connectorMode = column[String]("CONNECTOR_MODE")

  def connectorId = column[Int]("CONNECTOR_ID")

  def collectId = column[Int]("COLLECT_ID")

  def collectArgs = column[Option[String]]("COLLECT_ARGS")

  def formatAnalyzeId = column[Option[Int]]("FORMAT_ANALYZE_ID")

  def formatAnalyzeArgs = column[Option[String]]("FORMAT_ANALYZE_ARGS")

  def formatAlarmId = column[Option[Int]]("FORMAT_ALARM_ID")

  def formatAlarmArgs = column[Option[String]]("FORMAT_ANALYZE_ARGS")

  override def * = (id, cron, connectorMode, connectorId, collectId, collectArgs, formatAnalyzeId, formatAnalyzeArgs, formatAlarmId, formatAlarmArgs) <> (CoreMonitorDetail.tupled, CoreMonitorDetail.unapply)
}