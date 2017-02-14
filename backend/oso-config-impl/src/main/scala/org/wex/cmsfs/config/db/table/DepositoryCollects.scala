package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.DepositoryCollect
import slick.driver.MySQLDriver.api._

class DepositoryCollects(tag: Tag) extends Table[DepositoryCollect](tag, "depository_collect") {

  def id = column[Option[Int]]("ID")

  def detailId = column[Int]("detail_id")

  def connector = column[String]("CONNECTOR") // JDBC or SSH

  def monitor = column[String]("MONITOR")

  def data = column[String]("DATA")

  override def * = (id, detailId, connector, monitor, data) <> (DepositoryCollect.tupled, DepositoryCollect.unapply)
}
