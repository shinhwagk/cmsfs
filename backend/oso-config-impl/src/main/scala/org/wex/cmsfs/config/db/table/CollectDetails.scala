package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.db.Tables._
import org.wex.cmsfs.config.api.CollectDetail
import slick.driver.MySQLDriver.api._

class CollectDetails(tag: Tag) extends Table[CollectDetail](tag, "collect_detail") {

  def id = column[Int]("ID")

  def mode = column[String]("MODE") // JDBC or SSH

  def monitorId = column[Int]("MONITOR_ID")

  def connectorId = column[Int]("CONNECTOR_ID")

  def cron = column[String]("CRON")

  def args = column[Seq[String]]("ARGS")

  override def * = (id, mode, monitorId, connectorId, cron, args) <> (CollectDetail.tupled, CollectDetail.unapply)
}