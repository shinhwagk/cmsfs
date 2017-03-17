package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.Machine
import slick.jdbc.MySQLProfile.api._

class Machines(tag: Tag) extends Table[Machine](tag, "machine") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def tags = column[String]("TAGS")

  def ip = column[String]("IP")

  def state = column[Boolean]("STATE")

  override def * = (id, name, tags, ip, state) <> (Machine.tupled, Machine.unapply)
}