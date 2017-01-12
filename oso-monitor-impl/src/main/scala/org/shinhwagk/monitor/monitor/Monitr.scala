package org.shinhwagk.monitor.monitor

import java.util.Date
import java.util.Locale.Category

import org.quartz.CronExpression
import org.shinhwagk.config.api.ConfigService

/**
  * Created by zhangxu on 2017/1/12.
  */

object Category extends Enumeration {
  type MonitorItemEnum = Value
  val ORACLE = Value("ORACLE")
  val LINUX = Value("LINUX")
}

trait Monitor {
  val monId: Int
  val monName: String
  val monLabel: String
  val cron: String
  val category: Category.MonitorItemEnum
  val persistence: Boolean
}

trait Machine {
  val macId: Int
  val macName: String
  val macLabel: String
  val ip: String
}

trait MonitorContentOracle {
  val sqlText: String
  val defaultArgs: List[Any]
}

trait MonitorContentOS {
  val shell: String
  val args: List[Any]
}

trait MachineOracle extends Machine {
  val macOracleId: Int
  val jdbcUrl: String
  val port: Int
  val name: String
}

trait MachineOS extends Machine {
  val macOsId: Int
  val port: Int
  val name: String
}

case class MonitorOracle(monitor: Monitor,
                         machine: MachineOracle,
                         monitorContent: MonitorContentOracle,
                         customArgs: List[Any])


case class MonitorOS(monitor: Monitor,
                     machine: MachineOS,
                     monitorContent: MonitorContentOS,
                     customArgs: List[Any])

//
//trait MonitorOS extends Monitor {
//  val machine: MachineOS
//  val monitorContent: MonitorContentOS
//  val monitorArgs: List[Any]
//}

object Test {

  case class QueryOracleMessage(jdbcUrl: String, username: String, password: String, sqlText: String, parameters: List[Any])

  def sendMonitor(f: Int => String, url: String): Unit = {

  }

  val c: List[Monitor] = Nil

  def exec(s: ConfigService) {
    c.filter(mon => new CronExpression(mon.cron).isSatisfiedBy(new Date()))
      .foreach((mon: Monitor) => {
        mon.category match {
          case Category.ORACLE =>
          case Category.LINUX => mo
        }
      })
  }


}