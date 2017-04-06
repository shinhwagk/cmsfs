package org.wex.cmsfs.config.db

import org.wex.cmsfs.config.api.CoreMonitorDetail
import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

class CoreMonitorDetails(tag: Tag) extends Table[CoreMonitorDetail](tag, "core_monitor_detail") {
  implicit val seqIntColumnType = MappedColumnType.base[Seq[Int], String](
    { b => Json.toJson(b).toString() }, { i => Json.parse(i).as[Seq[Int]] }
  )

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def cron = column[String]("CRON")

  def connectorMode = column[String]("CONNECTOR_MODE")

  def connectorId = column[Int]("CONNECTOR_ID")

  def collectId = column[Int]("COLLECT_ID")

  def collectArgs = column[Option[String]]("COLLECT_ARGS")

  def formatAnalyzeId = column[Option[Int]]("FORMAT_ANALYZE_ID")

  def formatAnalyzeArgs = column[Option[String]]("FORMAT_ANALYZE_ARGS")

  def formatAlarmIds = column[Seq[Int]]("FORMAT_ALARM_IDS")

  override def * = (id, cron, connectorMode, connectorId, collectId, collectArgs, formatAnalyzeId, formatAnalyzeArgs, formatAlarmIds) <> (CoreMonitorDetail.tupled, CoreMonitorDetail.unapply)
}