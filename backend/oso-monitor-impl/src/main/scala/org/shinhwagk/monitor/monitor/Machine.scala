package org.shinhwagk.monitor.monitor

/**
  * Created by zhangxu on 2017/1/13.
  */
sealed trait Machine {
  val machine_id: Int
}

trait MachineItemJdbc extends Machine {
  val jdbcUrl: String
  val username: String
  val password: String
}

case class MachineItemJdbcImpl(machine_id: Int, //monitor_rdb.id
                               jdbcUrl: String,
                               username: String,
                               password: String,
                               category: MonitorItemEnum.Category) extends MachineItemJdbc

object MachineItemRdbOracle {
  def apply(id: Int,
            jdbcUrl: String,
            username: String,
            password: String): MachineItemJdbc =
    MachineItemJdbcImpl(id, jdbcUrl, username, password, MonitorItemEnum.ORACLE)
}

object MachineItemRdbMysql {
  def apply(id: Int,
            jdbcUrl: String,
            username: String,
            password: String): MachineItemJdbc =
    MachineItemJdbcImpl(id, jdbcUrl, username, password, MonitorItemEnum.MYSQL)
}

trait MachineItemSSH extends Machine {
  val ip: String
  val port: Int
  val user: String
  val possword: Option[String]
  val pKey: Option[String]
  val category: MonitorItemEnum.Category
}

case class MachineItemSSHImp(machine_id: Int,
                             ip: String,
                             port: Int,
                             user: String,
                             possword: Option[String],
                             pKey: Option[String],
                             category: MonitorItemEnum.Category) extends MachineItemSSH

object MachineItemSSHLinux {
  def apply(id: Int,
            ip: String,
            port: Int,
            user: String,
            possword: Option[String],
            pKey: Option[String] = None): MachineItemSSH =
    MachineItemSSHImp(id, ip, port, user, possword, pKey, MonitorItemEnum.LINUX)
}