package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.CoreMonitorDetail
import slick.jdbc.MySQLProfile.api._

class CoreMonitorDetails(tag: Tag) extends Table[CoreMonitorDetail](tag, "core_monitor_detail") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def cron = column[String]("CRON")

  def connectorMode = column[String]("CONNECTOR_MODE")

  def connectorId = column[Int]("CONNECTOR_ID")

  def collectId = column[Int]("COLLECT_ID")

  override def * = (id, cron, connectorMode, connectorId, collectId) <> (CoreMonitorDetail.tupled, CoreMonitorDetail.unapply)
}