package org.shinhwagk.monitor.monitor

/**
  * Created by zhangxu on 2017/1/13.
  */

sealed trait Monitor {
  val monitor_id: Int
  val cron: String
  val persistence: Boolean
}

trait MonitorItemRdb extends Monitor {
  val sqlText: String
  val args: List[Any]
}

case class MonitorItemRdbImpl(monitor_id: Int, //monitor_rdb.id
                              cron: String,
                              persistence: Boolean,
                              sqlText: String,
                              args: List[Any],
                              category: MonitorItemEnum.Category) extends MonitorItemRdb

object MonitorItemRdbOracle {
  def apply(monitor_id: Int,
            cron: String,
            sqlText: String,
            args: List[Any],
            persistence: Boolean): MonitorItemRdb =
    MonitorItemRdbImpl(monitor_id, cron, persistence, sqlText, args, MonitorItemEnum.ORACLE)
}

object MonitorItemRdbMysql {
  def apply(monitor_id: Int,
            cron: String,
            sqlText: String,
            args: List[Any],
            persistence: Boolean): MonitorItemRdb =
    MonitorItemRdbImpl(monitor_id, cron, persistence, sqlText, args, MonitorItemEnum.MYSQL)
}

trait MonitorItemHost extends Monitor {
  val script: String
  val args: List[Any]
}

case class MonitorItemHostImpl(monitor_id: Int, //monitor_rdb.id
                               cron: String,
                               persistence: Boolean,
                               script: String,
                               args: List[Any],
                               category: MonitorItemEnum.Category) extends MonitorItemHost


object MonitorItemHostLinux {
  def apply(monitor_id: Int, //monitor_rdb.id
            cron: String,
            persistence: Boolean,
            script: String,
            args: List[Any]): MonitorItemHost =
    MonitorItemHostImpl(monitor_id, cron, persistence, script, args, MonitorItemEnum.LINUX)
}
