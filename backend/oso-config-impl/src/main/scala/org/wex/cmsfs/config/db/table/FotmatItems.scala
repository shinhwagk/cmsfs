package org.wex.cmsfs.config.db.table


import org.wex.cmsfs.config.api.FormatItem
import slick.driver.MySQLDriver.api._

class FotmatItems(tag: Tag) extends Table[FormatItem](tag, "format_item") {

  def id = column[Int]("ID")

  def name = column[String]("NAME")

  def url = column[String]("URL")

  override def * = (id, name, url) <> (FormatItem.tupled, FormatItem.unapply)
}