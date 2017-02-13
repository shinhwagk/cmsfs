package org.shinhwagk.config.db.table

import org.shinhwagk.config.api.MonitorModeJDBC
import org.shinhwagk.config.db.Tables._
import slick.driver.MySQLDriver.api._

class MonitorModeJDBCs(tag: Tag) extends Table[MonitorModeJDBC](tag, "monitor_mode_jdbc") {

  def id = column[Option[Int]]("ID")

  def code = column[String]("CODE")

  def args = column[Seq[String]]("ARGS")

  override def * = (id, code, args) <> (MonitorModeJDBC.tupled, MonitorModeJDBC.unapply)
}