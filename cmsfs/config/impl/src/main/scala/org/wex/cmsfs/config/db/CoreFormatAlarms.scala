package org.wex.cmsfs.config.db

import org.wex.cmsfs.config.api.CoreFormatAlarm
import slick.jdbc.MySQLProfile.api._

class CoreFormatAlarms(tag: Tag) extends Table[CoreFormatAlarm](tag, "core_format_alarm") {

  def id = column[Option[Int]]("ID")

  def path = column[String]("PATH")

  def args = column[String]("ARGS")

  def notification = column[String]("NOTIFICATION")

  override def * = (id, path, args, notification) <> (CoreFormatAlarm.tupled, CoreFormatAlarm.unapply)
}