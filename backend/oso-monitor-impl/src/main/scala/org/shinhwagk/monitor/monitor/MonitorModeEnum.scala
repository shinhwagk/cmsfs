package org.shinhwagk.monitor.monitor

/**
  * Created by zhangxu on 2017/1/22.
  */
object MonitorModeEnum extends Enumeration {
  type MonitorModeEnum = Value
  val JDBC = Value("JDBC")
  val SSH = Value("SSH")

}
