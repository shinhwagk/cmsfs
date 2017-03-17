package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.CoreConnectorSsh
import slick.jdbc.MySQLProfile.api._

class CoreConnectorSshs(tag: Tag) extends Table[CoreConnectorSsh](tag, "core_connector_ssh") {

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def ip = column[String]("IP")

  def port = column[Int]("PORT")

  def user = column[String]("USER")

  def password = column[Option[String]]("PASSWORD")

  def privateKey = column[Option[String]]("PRIVATE_KEY")

  override def * = (id, name, ip, port, user, password, privateKey) <> (CoreConnectorSsh.tupled, CoreConnectorSsh.unapply)
}
