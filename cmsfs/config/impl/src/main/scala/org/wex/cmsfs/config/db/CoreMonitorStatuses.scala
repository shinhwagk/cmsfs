package org.wex.cmsfs.config.db

import org.wex.cmsfs.config.api.CoreMonitorStatus
import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

class CoreMonitorStatuses(tag: Tag) extends Table[CoreMonitorStatus](tag, "core_monitor_status") {
//
//  implicit val boolColumnType = MappedColumnType.base[Map[String, List[String]], String](
//    { b => Json.toJson(b).toString() }, { i => Json.parse(i).as[Map[String, List[String]]] }
//  )

  def id = column[Int]("ID", O.PrimaryKey)

  def category = column[String]("CATEGORY")

  def metric = column[String]("METRIC")

  def status = column[String]("STATUS")

  override def * = (id, category, metric, status) <> (CoreMonitorStatus.tupled, CoreMonitorStatus.unapply)
}