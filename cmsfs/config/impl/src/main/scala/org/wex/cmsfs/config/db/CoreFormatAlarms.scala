package org.wex.cmsfs.config.db

import org.wex.cmsfs.config.api.{CoreFormatAlarm, CoreNotification}
import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

class CoreFormatAlarms(tag: Tag) extends Table[CoreFormatAlarm](tag, "core_format_alarm") {

  implicit val notificationIntColumnType = MappedColumnType.base[CoreNotification, String](
    { b => Json.toJson(b).toString() }, { i => Json.parse(i).as[CoreNotification] }
  )

  def id = column[Option[Int]]("ID")

  def path = column[String]("PATH")

  def args = column[String]("ARGS")

  def notification = column[CoreNotification]("NOTIFICATION")

  override def * = (id, path, args, notification) <> (CoreFormatAlarm.tupled, CoreFormatAlarm.unapply)
}