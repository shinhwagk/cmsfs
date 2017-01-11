package org.shinhwagk.config.db

import org.shinhwagk.config.api.Host
import play.api.libs.json.Json
import slick.driver.MySQLDriver.api._

/**
  * Created by zhangxu on 2017/1/10.
  */
object Tables {

  implicit val tagsMapper = MappedColumnType.base[List[String], String](
    Json.toJson(_).toString(),
    Json.parse(_).as[List[String]]
  )

  class Hosts(tag: Tag) extends Table[Host](tag, "host") {
    def id = column[Option[Int]]("ID", O.PrimaryKey)

    def label = column[String]("LABEL")

    def hostname = column[String]("HOSTNAME")

    def ip = column[String]("IP")

    def port = column[Int]("PORT")

    def tags = column[List[String]]("TAGS")

    def status = column[Boolean]("STATUS")

    def * = (id, label, hostname, ip, port, tags, status) <> (Host.tupled, Host.unapply)
  }

  val hosts = TableQuery[Hosts]
}
