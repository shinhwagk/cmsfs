package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.MonitorDepository
import slick.jdbc.MySQLProfile.api._

class MonitorDepositories(tag: Tag) extends Table[MonitorDepository](tag, "monitor_depository") {

  def id = column[Option[Long]]("ID", O.AutoInc, O.PrimaryKey)

  def monitorId = column[Long]("monitor_id")

  def collectData = column[String]("collect")

  def analyzeData = column[Option[String]]("analyze")

  def alarmData = column[Option[String]]("alarm")

  override def * = (id, monitorId, collectData, analyzeData, alarmData) <> (MonitorDepository.tupled, MonitorDepository.unapply)
}
