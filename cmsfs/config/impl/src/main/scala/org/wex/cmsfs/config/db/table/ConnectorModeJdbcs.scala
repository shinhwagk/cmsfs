package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.db.Tables._
import org.wex.cmsfs.config.api.ConnectorModeJDBC
import slick.jdbc.MySQLProfile.api._

class ConnectorModeJdbcs(tag: Tag) extends Table[ConnectorModeJDBC](tag, "connector_jdbc") {

  def id = column[Option[Int]]("ID")

  def machineId = column[Int]("MACHINE_ID")

  def tags = column[Seq[String]]("tags")

  def name = column[String]("name")

  def url = column[String]("URL")

  def user = column[String]("USER")

  def password = column[String]("PASSWORD")

  def category = column[String]("CATEGORY")

  def categoryVersion = column[String]("CATEGORY_VERSION")

  def state = column[Boolean]("STATE")

  override def * = (id, machineId, tags, name, url, user, password, category, categoryVersion, state) <> (ConnectorModeJDBC.tupled, ConnectorModeJDBC.unapply)
}