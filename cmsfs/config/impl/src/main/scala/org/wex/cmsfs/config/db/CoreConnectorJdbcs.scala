package org.wex.cmsfs.config.db

import org.wex.cmsfs.config.api.CoreConnectorJdbc
import slick.jdbc.MySQLProfile.api._

class CoreConnectorJdbcs(tag: Tag) extends Table[CoreConnectorJdbc](tag, "core_connector_jdbc") {

  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def url = column[String]("URL")

  def user = column[String]("USER")

  def password = column[String]("PASSWORD")

  override def * = (id, name, url, user, password) <> (CoreConnectorJdbc.tupled, CoreConnectorJdbc.unapply)
}