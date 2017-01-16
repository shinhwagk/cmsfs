package org.shinhwagk.config.db

import org.shinhwagk.config.api.MonitorCategoryEnum.MonitorCategoryEnum
import org.shinhwagk.config.api.{Host, MonitorCategoryEnum, MonitorDetail}
import play.api.libs.json.Json
import slick.driver.MySQLDriver.api._

/**
  * Created by zhangxu on 2017/1/10.
  */
object Tables {

  private type StringList = List[String]

  private implicit val categoryMapper = MappedColumnType.base[MonitorCategoryEnum, String](
    _ match {
      case MonitorCategoryEnum.JDBC => "ORACLE"
      case MonitorCategoryEnum.SSH => "OS"
    },
    _ match {
      case "ORACLE" => MonitorCategoryEnum.JDBC
      case "OS " => MonitorCategoryEnum.SSH
    }
  )

  private implicit val tagsMapper = MappedColumnType.base[StringList, String](
    Json.toJson(_).toString(),
    Json.parse(_).as[StringList]
  )

  //  class Monitors(tag: Tag) extends Table[Monitor](tag, "monitors") {
  //    def id = column[Option[Int]]("SUP_ID", O.PrimaryKey, O.AutoInc)
  //
  //    def category = column[MonitorCategoryEnum]("category")
  //
  //    def label = column[String]("label")
  //
  //    def args = column[StringList]("args")
  //
  //    def tags = column[StringList]("tags")
  //
  //    def state = column[Boolean]("state")
  //
  //    def * = (id, category, label, args, tags, state) <> (Monitor.tupled, Monitor.unapply)
  //  }

  class Machines(tag: Tag) extends Table[Host](tag, "host") {
    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

    def label = column[String]("LABEL")

    def hostname = column[String]("HOSTNAME")

    def ip = column[String]("IP")

    def port = column[Int]("PORT")

    def tags = column[StringList]("TAGS")

    def status = column[Boolean]("STATUS")

    override def * = (id, label, hostname, ip, port, tags, status) <> (Host.tupled, Host.unapply)
  }

  val hosts = TableQuery[Machines]

  class MonitorDetails(tag: Tag) extends Table[MonitorDetail](tag, "monitor_item_details") {
    def id = column[Int]("MONITOR_DETAIL_ID", O.PrimaryKey, O.AutoInc)

    def monitor_item_id = column[Int]("MONITOR_ITEM_ID")

    def machine_item_id = column[Int]("MACHINE_ITEM_ID")

    def args = column[List[String]]("ARGS")

    def cron = column[String]("CRON")

    def mode = column[String]("MODE")

    override def * = (id, monitor_item_id, machine_item_id, args, cron, mode) <> (MonitorDetail.tupled, MonitorDetail.unapply)
  }

  val monitorDetails = TableQuery[MonitorDetails]

}
