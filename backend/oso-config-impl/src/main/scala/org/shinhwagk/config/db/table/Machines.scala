package org.shinhwagk.config.db.table

import org.shinhwagk.config.api.Machine
import slick.driver.MySQLDriver.api._

class Machines(tag: Tag) extends Table[Machine](tag, "machine") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def tags = column[String]("TAGS")

  def ip = column[String]("IP")

  def state = column[Boolean]("STATE")

  override def * = (id, name, tags, ip, state) <> (Machine.tupled, Machine.unapply)
}