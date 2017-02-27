package org.wex.cmsfs.monitor.api

import play.api.libs.json.{Format, Json}

case class MonitorItem(id: Int)

case object MonitorItem {
  implicit val format: Format[MonitorItem] = Json.format
}

case class MonitorActionForJDBC(id: Int, metricName: String, collectArgs: Option[Seq[String]], analyzeArgs: Option[Seq[String]],
                                url: String, user: String, password: String)

object MonitorActionForJDBC {
  implicit val format: Format[MonitorActionForJDBC] = Json.format
}

case class MonitorActionForSSH(id: Int, metricName: String, collectArgs: Option[Seq[String]], analyzeArgs: Option[Seq[String]],
                               ip:String,port: Int, user: String, password: Option[String], privateKey: Option[String])

object MonitorActionForSSH {
  implicit val format: Format[MonitorActionForSSH] = Json.format
}