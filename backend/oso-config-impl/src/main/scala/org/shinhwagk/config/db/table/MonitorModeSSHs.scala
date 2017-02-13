package org.shinhwagk.config.db.table

import org.shinhwagk.config.api.MonitorModeSSH
import org.shinhwagk.config.db.Tables._
import slick.driver.MySQLDriver.api._

class MonitorModeSSHs(tag: Tag) extends Table[MonitorModeSSH](tag, "monitor_mode_ssh") {

  def id = column[Option[Int]]("ID")

  def code = column[String]("CODE")

  def args = column[Seq[String]]("ARGS")

  override def * = (id, code, args) <> (MonitorModeSSH.tupled, MonitorModeSSH.unapply)
}