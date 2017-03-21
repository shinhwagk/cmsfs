package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.{CoreFormatAlarm, CoreFormatAnalyze}
import slick.jdbc.MySQLProfile.api._

class CoreFormatAlarms(tag: Tag) extends Table[CoreFormatAlarm](tag, "core_format_alarm") {

  def id = column[Option[Int]]("ID")

  def path = column[String]("PATH")

  def name = column[String]("NAME")

  def args = column[String]("ARGS")

  override def * = (id, path, name, args) <> (CoreFormatAlarm.tupled, CoreFormatAlarm.unapply)
}