package org.shinhwagk.config.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import org.shinhwagk.config.api.MonitorCategoryEnum.MonitorCategoryEnum
import play.api.libs.json._

/**
  * Created by zhangxu on 2017/1/10.
  */
trait ConfigService extends Service {

  /**
    * monitors
    */
  def getMonitorDetails: ServiceCall[NotUsed, List[MonitorDetail]]

  def getMonitorItem(mode: String, id: Int): ServiceCall[Int, List[MonitorDetail]]

  /**
    * machine
    */
  def addMachine: ServiceCall[Machine, NotUsed]

  def getMachines: ServiceCall[NotUsed, List[Machine]]

  def getMachineConnectorModeJdbc(id: Int): ServiceCall[NotUsed, MachineConnectorModeJDBC]

  def addConnector: ServiceCall[Connector, NotUsed]

  def getConnectors: ServiceCall[NotUsed, List[Connector]]

  def addMonitor: ServiceCall[Monitor, NotUsed]

  def addMonitorMode(mode: String): ServiceCall[String, NotUsed]

  def getMonitorList: ServiceCall[NotUsed, List[Monitor]]

  def getMonitorMode(mode: String, id: Int): ServiceCall[NotUsed, String]

  def addMonitorPersistence: ServiceCall[MonitorPersistence, NotUsed]

  def getMonitorPersistenceContent(id: Long, version: Long): ServiceCall[NotUsed, String]


  /**
    * alarm
    */
  def getAlarmDetails(aId: Int): ServiceCall[NotUsed, List[MonitorAlarmDetail]]

  def getAlarm(id: Int): ServiceCall[NotUsed, MonitorAlarm]

  //  def test(id: Long, version: Long): ServiceCall[NotUsed, String]

  override final def descriptor = {
    named("oso-config").withCalls(
      //      restCall(Method.GET, "/v1/node/:id", getHost _),
      //      restCall(Method.GET, "/v1/nodes", getHosts),
      //      restCall(Method.PUT, "/v1/node/:id", putHost _),
      //      restCall(Method.DELETE, "/v1/node/:id", deleteHost _),
      //      restCall(Method.POST, "/v1/node", addHost),

      restCall(Method.GET, "/v1/machine/connector/jdbc/:id", getMachineConnectorModeJdbc _),

      restCall(Method.GET, "/v1/monitor/details", getMonitorDetails),

      restCall(Method.GET, "/v1/monitor/jdbc/:id", getMonitorMode _),

      restCall(Method.GET, "/v1/machines", getMachines),

      restCall(Method.POST, "/v1/machine", addMachine),

      restCall(Method.POST, "/v1/machine/connector", addConnector),

      restCall(Method.POST, "/v1/monitor", addMonitor),

//      restCall(Method.POST, "/v1/monitor/add/:mode", addMonitorMode _),


      restCall(Method.POST, "/v1/monitor/list", getMonitorList),

      restCall(Method.POST, "/v1/monitor/persistence", addMonitorPersistence _),

      restCall(Method.GET, "/v1/monitor/persistence/:id/:version", getMonitorPersistenceContent _),

      restCall(Method.GET, "/v1/monitor/alarm/detail/:mid", getAlarmDetails _),

      restCall(Method.GET, "/v1/monitor/alarm/:id", getAlarm _)
    )
  }
}

object MonitorCategoryEnum extends Enumeration {
  type MonitorCategoryEnum = Value
  val SSH = Value("SSH")
  val JDBC = Value("JDBC")
}


//case class MonitorDetailShort(id: Int, mode: String)
//
//object MonitorDetailShort {
//  implicit val format: Format[MonitorDetailShort] = Json.format[MonitorDetailShort]
//}
//
//case class MonitorDetail(id: Int, monitor_item_id: Int, machine_item_id: Int, args: List[String], mode: String, cron: String)
//
//
//object MonitorDetail extends ((Int, Int, Int, List[String], String, String) => MonitorDetail) {
//
//  implicit val format: Format[MonitorDetail] = Json.format[MonitorDetail]
//}
//

//case class Monitor(id: Option[Int], category: MonitorCategoryEnum, label: String, args: List[String], tags: List[String], state: Boolean)
//
//object Monitor extends ((Option[Int], MonitorCategoryEnum, String, List[String], List[String], Boolean) => Monitor) {
//
//  implicit object MonitorEnumJsonFormat extends Format[MonitorCategoryEnum] {
//    override def reads(json: JsValue): JsResult[MonitorCategoryEnum] = json match {
//      case JsString(s) if s == MonitorCategoryEnum.ORACLE.toString => json.validate[MonitorCategoryEnum]
//      case JsString(s) if s == MonitorCategoryEnum.OS.toString => json.validate[MonitorCategoryEnum]
//      case _ => throw new Exception("MonitorEnum match error")
//    }
//
//    override def writes(o: MonitorCategoryEnum): JsValue = o match {
//      case MonitorCategoryEnum.ORACLE => JsString("ORACLE")
//      case MonitorCategoryEnum.OS => JsString("OS")
//    }
//  }
//
//  implicit val format: Format[Monitor] = Json.format[Monitor]
//}

//case class RDatabase(id: Int, hid: Int, jdbcUrl: String, user: String, password: String, status: Boolean, tag: List[String])
//
//object RDatabase {
//  implicit val format: Format[RDatabase] = Json.format[RDatabase]
//}

case class Machine(id: Option[Int], name: String, label: String, ip: String, state: Boolean)

object Machine extends ((Option[Int], String, String, String, Boolean) => Machine) {
  implicit val format: Format[Machine] = Json.format[Machine]
}

case class Connector(id: Option[Int], mheId: Int, label: String,
                     category: String, categoryVersion: String,
                     mode: String, modeInfo: String, state: Boolean)

object Connector extends ((Option[Int], Int, String, String, String, String, String, Boolean) => Connector) {
  implicit val format: Format[Connector] = Json.format[Connector]
}

case class Monitor(id: Option[Int],
                   name: String,
                   label: String,
                   cron: String,
                   mode: String,
                   modeId: Int,
                   persistence: Boolean,
                   state: Boolean)

object Monitor extends ((Option[Int], String, String, String, String, Int, Boolean, Boolean) => Monitor) {

  implicit object AnyJsonFormat extends Format[Any] {
    override def reads(json: JsValue): JsResult[Any] = json match {
      case JsBoolean(true) => json.validate[Boolean]
      case JsBoolean(false) => json.validate[Boolean]
      case JsString(_) => json.validate[String]
      case JsNumber(_) => json.validate[Int]
      case _ => throw new Exception("match error")
    }

    override def writes(o: Any): JsValue = o match {
      case i: Int => JsNumber(i)
      case s: String => JsString(s)
      case t: Boolean if t => JsBoolean(true)
      case f: Boolean if !f => JsBoolean(false)
    }
  }

  implicit val format: Format[Monitor] = Json.format[Monitor]
}

case class MonitorModeJDBC(id: Option[Int], category: String, categoryVersion: String, code: String)

object MonitorModeJDBC extends ((Option[Int], String, String, String) => MonitorModeJDBC) {
  implicit val format: Format[MonitorModeJDBC] = Json.format[MonitorModeJDBC]
}

case class MonitorModeSSH(id: Option[Int], category: String, categoryVersion: String, code: String)


case class MonitorDetail(id: Option[Int], mode: String, monitorModeId: Int, machineConnectorId: Int, cron: String, persistence: Boolean, alarm: Boolean, chart: Boolean)

object MonitorDetail extends ((Option[Int], String, Int, Int, String, Boolean, Boolean, Boolean) => MonitorDetail) {
  implicit val format: Format[MonitorDetail] = Json.format[MonitorDetail]
}

case class MachineConnectorModeJDBC(id: Option[Int], category: String, CategoryVersion: String, connector_id: Int, jdbcUrl: String, username: String, password: String)

object MachineConnectorModeJDBC extends ((Option[Int], String, String, Int, String, String, String) => MachineConnectorModeJDBC) {
  implicit val format: Format[MachineConnectorModeJDBC] = Json.format[MachineConnectorModeJDBC]
}

case class MachineConnectorModeSSH(id: Option[Int], category: String, CategoryVersion: String, connector_id: Int, sshPort: Int, username: String, password: String, privateKey: String)

object MachineConnectorModeSSH extends ((Option[Int], String, String, Int, Int, String, String, String) => MachineConnectorModeSSH) {
  implicit val format: Format[MachineConnectorModeSSH] = Json.format[MachineConnectorModeSSH]
}

case class MonitorPersistence(id: Option[Int], stage: String, version: Long, result: String, monitorDetailId: Int)

object MonitorPersistence extends ((Option[Int], String, Long, String, Int) => MonitorPersistence) {
  implicit val format: Format[MonitorPersistence] = Json.format[MonitorPersistence]
}


case class MonitorAlarm(id: Option[Int], script: String, state: Boolean, args: String)

object MonitorAlarm extends ((Option[Int], String, Boolean, String) => MonitorAlarm) {
  implicit val format: Format[MonitorAlarm] = Json.format[MonitorAlarm]
}

case class MonitorAlarmDetail(id: Option[Int], alarmId: Int, args: List[String], mode: String)

object MonitorAlarmDetail extends ((Option[Int], Int, List[String], String) => MonitorAlarmDetail) {
  implicit val format: Format[MonitorAlarmDetail] = Json.format[MonitorAlarmDetail]
}