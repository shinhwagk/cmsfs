package org.shinhwagk.config.store

import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by zhangxu on 2017/1/10.
  */
object Tables {
  val db = Database.forConfig("mysql")
//  import dbConfig.driver.api._

  def main(args: Array[String]): Unit = {
    val b = db.run(hosts.result)
    b.map(p=>println(p))
  }

  implicit val stringListMapper = MappedColumnType.base[List[String],String](
    list => list.mkString(","),
    string => string.split(',').toList
  )

  case class Host(id: Int, hostname: String, ip: String, port: Int, tags: List[String], status: Boolean)

  class Hosts(tag: Tag) extends Table[Host](tag, "Host") {
    def id = column[Int]("ID", O.PrimaryKey)

    def hostname = column[String]("HOSTNAME")

    def ip = column[String]("IP")

    def port = column[Int]("PORT")

    def tags = column[List[String]]("TAGS")

    def status = column[Boolean]("STATUS")

    def * = (id, hostname, ip, port, tags, status) <> (Host.tupled, Host.unapply)
  }

  val hosts = TableQuery[Hosts]

}
