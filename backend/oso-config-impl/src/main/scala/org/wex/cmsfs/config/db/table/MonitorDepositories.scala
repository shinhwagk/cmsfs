package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.MonitorDepository
import slick.driver.MySQLDriver.api._

class MonitorDepositories(tag: Tag) extends Table[MonitorDepository](tag, "monitor_depository") {

  def id = column[Option[Long]]("ID", O.AutoInc, O.PrimaryKey)

  def monitorId = column[Int]("monitor_id")

  def collectData = column[String]("collect")

  def analyzeData = column[Option[String]]("analyze")

  def alarmData = column[Option[String]]("alarm")

  def timestamp = column[Long]("TIMESTAMP")

  override def * = (id, monitorId, collectData, analyzeData, alarmData, timestamp) <> (MonitorDepository.tupled, MonitorDepository.unapply)
}
