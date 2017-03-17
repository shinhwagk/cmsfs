package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.db.Tables._
import org.wex.cmsfs.config.api.ConnectorModeSSH
import slick.jdbc.MySQLProfile.api._

class ConnectorModeSSHs(tag: Tag) extends Table[ConnectorModeSSH](tag, "connector_ssh") {

  def id = column[Option[Int]]("ID")

  def machineId = column[Int]("MACHINE_ID")

  def tags = column[Seq[String]]("TAGS")

  def name = column[String]("NAME")

  def port = column[Int]("PORT")

  def user = column[String]("USER")

  def password = column[Option[String]]("PASSWORD")

  def privateKey = column[Option[String]]("PRIVATE_KEY")

  def category = column[String]("CATEGORY")

  def categoryVersion = column[String]("CATEGORY_VERSION")

  def state = column[Boolean]("STATE")

  override def * = (id, machineId, tags, name, port, user, password, privateKey, category, categoryVersion, state) <> (ConnectorModeSSH.tupled, ConnectorModeSSH.unapply)
}
