package org.shinhwagk.monitor.monitor

/**
  * Created by zhangxu on 2017/1/13.
  */
sealed trait Monitor {
  val monitor_id: Int
  val cron: String
  val persistence: Boolean
}

object MonitorItemRdb extends Enumeration {
  type Category = Value
  val ORACLE = Value("oracle")
  val MYSQL = Value("mysql")
}

case class MonitorItemRdb(monitor_id: Int, //monitor_rdb.id
                          cron: String,
                          persistence: Boolean,
                          category: MonitorItemRdb.Category) extends Monitor

object MonitorItemRdbOracle {
  def apply(monitor_id: Int,
            cron: String,
            persistence: Boolean,
            category: MonitorItemRdb.Category = MonitorItemRdb.ORACLE): Monitor = MonitorItemRdb(monitor_id, cron, persistence, category)
}

object MonitorItemRdbMysql {
  def apply(monitor_id: Int,
            cron: String,
            persistence: Boolean,
            category: MonitorItemRdb.Category = MonitorItemRdb.MYSQL): Monitor = MonitorItemRdb(monitor_id, cron, persistence, category)
}


object MonitorItemHost extends Enumeration {
  type Category = Value
  val LINUX = Value("linux")
  //  val WINDOWS = Value("windows")
}

case class MonitorItemHost(monitor_id: Int, //monitor_rdb.id
                           cron: String,
                           persistence: Boolean,
                           category: MonitorItemHost.Category) extends Monitor

object MonitorItemHostLinux {
  def apply(monitor_id: Int,
            cron: String,
            persistence: Boolean,
            category: MonitorItemRdb.Category = MonitorItemRdb.MYSQL): Monitor = MonitorItemRdb(monitor_id, cron, persistence, category)
}