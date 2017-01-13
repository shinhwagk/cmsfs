package org.shinhwagk.monitor.monitor

import org.shinhwagk.monitor.monitor.MachineItemRdbEnum.Category

/**
  * Created by zhangxu on 2017/1/13.
  */
sealed trait Machine {
  val machine_id: Int
}

object MachineItemRdbEnum extends Enumeration {
  type Category = Value
  val ORACLE = Value("oracle")
  val MYSQL = Value("mysql")
}

case class MachineItemRdb(machine_id: Int, //machine_rdb.id
                          jdbcUrl: Option[String],
                          username: String,
                          password: String,
                          category: MachineItemRdbEnum.Category) extends Machine

object MachineItemRdbOracle {
  def apply(id: Int,
            username: String,
            password: String,
            jdbcUrl: Option[String],
            category: Category = MachineItemRdbEnum.ORACLE): Machine = MachineItemRdb(id, jdbcUrl, username, password, category)
}

object MachineItemRdbMysql {
  def apply(id: Int,
            username: String,
            password: String,
            jdbcUrl: Option[String],
            category: Category = MachineItemRdbEnum.MYSQL): Machine = MachineItemRdb(id, jdbcUrl, username, password, category)
}


object MachineItemOSEnum extends Enumeration {
  type Category = Value
  val LINUX = Value("linux")
  //  val WINDOWS = Value("windows")
}

case class MachineItemHost(machine_id: Int, //machine_host.id
                           ip: String,
                           port: Int,
                           user: String,
                           possWord: Option[String],
                           pKey: Option[String],
                           category: MachineItemOSEnum.Category) extends Machine

object MachineItemHostLinux {
  def apply(id: Int,
            ip: String,
            port: Int,
            user: String,
            possWord: Option[String],
            pKey: Option[String],
            category: MachineItemOSEnum.Category = MachineItemOSEnum.LINUX): Machine = MachineItemHost(id, ip: String, port: Int, user: String, possWord, pKey, MachineItemOSEnum.LINUX)
}