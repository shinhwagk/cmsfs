package org.shinhwagk.monitor.monitor

/**
  * Created by gk on 2017/1/13.
  */
object MonitorItemEnum extends Enumeration {
  type Category = Value
  val ORACLE = Value("oracle")
  val MYSQL = Value("mysql")
  val LINUX = Value("linux")
}
