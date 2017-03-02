package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.MonitorDetail
import org.wex.cmsfs.config.db.Tables._
import slick.driver.MySQLDriver.api._

class MonitorDetails(tag: Tag) extends Table[MonitorDetail](tag, "monitor_detail") {

  def id = column[Int]("ID")

  def metricId = column[Int]("METRIC_ID")

  def connectorId = column[Int]("CONNECTOR_ID")

  def cron = column[String]("CRON")

  def collectArgs = column[Option[Seq[String]]]("COLLECT_ARGS")

  def analyzeArgs = column[Option[Seq[String]]]("ANALYZE_ARGS")

  def alarm = column[Boolean]("ALARM")

  override def * = (id, metricId, connectorId, cron, collectArgs, analyzeArgs, alarm) <> (MonitorDetail.tupled, MonitorDetail.unapply)
}