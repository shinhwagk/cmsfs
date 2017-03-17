package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.Metric
import org.wex.cmsfs.config.db.Tables._
import slick.jdbc.MySQLProfile.api._

class Metrics(tag: Tag) extends Table[Metric](tag, "metric") {

  def id = column[Option[Int]]("ID")

  def name = column[String]("NAME")

  def mode = column[String]("MODE")

  def cron = column[String]("CRON")

  def tags = column[Seq[String]]("TAGS")

  def category = column[String]("CATEGORY")

  def categoryVersion = column[Seq[String]]("CATEGORY_VERSION")

  def description = column[String]("DESCRIPTION")

  override def * = (id, name, mode, cron, tags, category, categoryVersion, description) <> (Metric.tupled, Metric.unapply)
}