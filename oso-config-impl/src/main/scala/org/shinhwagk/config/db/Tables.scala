package org.shinhwagk.config.db

import org.shinhwagk.config.api.MonitorCategoryEnum.MonitorCategoryEnum
import org.shinhwagk.config.api.{Host, Monitor, MonitorCategoryEnum}
import play.api.libs.json.Json
import slick.driver.MySQLDriver.api._

/**
  * Created by zhangxu on 2017/1/10.
  */
object Tables {

  private type StringList = List[String]

  private implicit val categoryMapper = MappedColumnType.base[MonitorCategoryEnum, String](
    _ match {
      case MonitorCategoryEnum.ORACLE => "ORACLE"
      case MonitorCategoryEnum.OS => "OS"
    },
    _ match {
      case "ORACLE" => MonitorCategoryEnum.ORACLE
      case "OS " => MonitorCategoryEnum.OS
    }
  )

  private implicit val tagsMapper = MappedColumnType.base[StringList, String](
    Json.toJson(_).toString(),
    Json.parse(_).as[StringList]
  )

  class Monitors(tag: Tag) extends Table[Monitor](tag, "monitors") {
    def id = column[Option[Int]]("SUP_ID", O.PrimaryKey, O.AutoInc)

    def category = column[MonitorCategoryEnum]("category")

    def label = column[String]("label")

    def args = column[StringList]("args")

    def tags = column[StringList]("tags")

    def state = column[Boolean]("state")

    def * = (id, category, label, args, tags, state) <> (Monitor.tupled, Monitor.unapply)
  }


  class Hosts(tag: Tag) extends Table[Host](tag, "host") {
    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

    def label = column[String]("LABEL")

    def hostname = column[String]("HOSTNAME")

    def ip = column[String]("IP")

    def port = column[Int]("PORT")

    def tags = column[StringList]("TAGS")

    def status = column[Boolean]("STATUS")

    def * = (id, label, hostname, ip, port, tags, status) <> (Host.tupled, Host.unapply)
  }

  val hosts = TableQuery[Hosts]
  val monitors = TableQuery[Monitors]
}
