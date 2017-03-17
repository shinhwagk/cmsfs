package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.CoreFormatAnalyze
import slick.jdbc.MySQLProfile.api._

class CoreFormatAnalyzes(tag: Tag) extends Table[CoreFormatAnalyze](tag, "core_format_analyze") {

  def id = column[Option[Int]]("ID")

  def path = column[String]("PATH")

  def name = column[String]("NAME")

  def args = column[String]("ARGS")

  def collectId = column[Int]("COLLECT_ID")

  override def * = (id, path, name, args, collectId) <> (CoreFormatAnalyze.tupled, CoreFormatAnalyze.unapply)
}

