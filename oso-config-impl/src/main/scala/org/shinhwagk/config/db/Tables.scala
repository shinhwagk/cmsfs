package org.shinhwagk.config.db

import org.shinhwagk.config.api.Host
import play.api.libs.json.Json
import slick.driver.MySQLDriver.api._

/**
  * Created by zhangxu on 2017/1/10.
  */
object Tables {
  case class Monitorx(id: Int)

  class Monitors(tag: Tag) extends Table[Monitorx](tag, "monitor") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    //    def category = column[MonitorCategoryEnum]("CATEGORY")
    //
    //    def label = column[String]("LABEL")
    //
    //    def args = column[List[String]]("ARGS")
    //
    //    def tags = column[List[String]]("TAGS")
    //
    //    def status = column[Boolean]("STATUS")

    def * = (id) <> (Monitorx.tupled, Monitorx.unapply)

    //      , label, category, args, tags, status

  }

  val monitors = TableQuery[Monitors]

  implicit val tagsMapper = MappedColumnType.base[List[String], String](
    Json.toJson(_).toString(),
    Json.parse(_).as[List[String]]
  )

  class Hosts(tag: Tag) extends Table[Host](tag, "host") {
    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

    def label = column[String]("LABEL")

    def hostname = column[String]("HOSTNAME")

    def ip = column[String]("IP")

    def port = column[Int]("PORT")

    def tags = column[List[String]]("TAGS")

    def status = column[Boolean]("STATUS")

    def * = (id, label, hostname, ip, port, tags, status) <> (Host.tupled, Host.unapply)
  }

  val hosts = TableQuery[Hosts]

  //  implicit val categoryMapper = MappedColumnType.base[MonitorCategoryEnum, String](
  //    _ match {
  //      case MonitorCategoryEnum.ORACLE => "ORACLE"
  //      case MonitorCategoryEnum.OS => "OS"
  //    },
  //    _ match {
  //      case "ORACLE" => MonitorCategoryEnum.ORACLE
  //      case "OS " => MonitorCategoryEnum.OS
  //    }
  //  )


}
