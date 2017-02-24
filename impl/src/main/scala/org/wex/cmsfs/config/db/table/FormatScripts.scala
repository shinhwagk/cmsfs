package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.FormatScript
import slick.driver.MySQLDriver.api._

class FormatScripts(tag: Tag) extends Table[FormatScript](tag, "format_script") {

  def id = column[Int]("ID")

  def category = column[String]("CATEGORY")

  def name = column[String]("NAME")

  def url = column[String]("URL")

  override def * = (id, category, name, url) <> (FormatScript.tupled, FormatScript.unapply)
}