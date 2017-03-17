package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.db.Tables._
import org.wex.cmsfs.config.api.MonitorModeSSH
import slick.jdbc.MySQLProfile.api._

class MonitorModeSSHs(tag: Tag) extends Table[MonitorModeSSH](tag, "monitor_mode_ssh") {

  def id = column[Option[Int]]("ID")

  def code = column[String]("CODE")

  def args = column[Seq[String]]("ARGS")

  override def * = (id, code, args) <> (MonitorModeSSH.tupled, MonitorModeSSH.unapply)
}