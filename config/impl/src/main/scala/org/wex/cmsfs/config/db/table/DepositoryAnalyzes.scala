package org.wex.cmsfs.config.db.table

import org.wex.cmsfs.config.api.DepositoryAnalyze
import slick.driver.MySQLDriver.api._

class DepositoryAnalyzes (tag: Tag) extends Table[DepositoryAnalyze](tag, "depository_analyze") {

    def id = column[Option[Int]]("ID")

    def collectId = column[Int]("collect_id")

    def data = column[String]("DATA")

    override def * = (id, collectId,  data) <> (DepositoryAnalyze.tupled, DepositoryAnalyze.unapply)
  }
