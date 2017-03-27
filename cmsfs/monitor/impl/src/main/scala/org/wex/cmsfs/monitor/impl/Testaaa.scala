package org.wex.cmsfs.monitor.impl

class Testaaa {

}

case class MonitorDetailForJdbc(id: Int,
                                connector: ConnectorJdbc, collect: Collect,
                                analyze: Option[FormatAnalyze],
                                alarm: Option[FormatAlarm])

case class MonitorDetailForSsh(id: Int, connector: ConnectorSsh, collect: Collect)

case class Collect(id: Int, name: String, path: String, args: Option[String])

case class ConnectorJdbc(id: Int, name: String, url: String, user: String, password: String)

case class ConnectorSsh(id: Int, name: String, ip: String, user: String, password: Option[String], privateKey: Option[String])

case class Elasticsearch(_index: String, _type: String, _id: Option[String] = None)

case class FormatAnalyze(id: Int, path: String, args: Option[String], elasticsearch: Elasticsearch)

/**
  *
  * map[mail -> seq(191631513@qq.com),phone -> seq(13818888888)]
  *
  */
case class Notification(args: String, sends: Map[String, Seq[String]])

case class FormatAlarm(id: Int, path: String, args: Option[String], _index: String, _type: String)