package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.CoreFormatAnalyze
import slick.jdbc.MySQLProfile.api._

class CoreFormatAnalyzes(tag: Tag) extends Table[CoreFormatAnalyze](tag, "core_format_analyze") {

  def id = column[Option[Int]]("ID")

  def path = column[String]("PATH")

  def args = column[String]("ARGS")

  def collectId = column[Int]("COLLECT_ID")

  def _index = column[String]("_INDEX")

  def _type = column[String]("_TYPE")

  def _metric = column[String]("_METRIC")

  override def * = (id, path, args, collectId, _index, _type, _metric) <> (CoreFormatAnalyze.tupled, CoreFormatAnalyze.unapply)
}

