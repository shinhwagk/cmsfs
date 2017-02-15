package org.wex.cmsfs.config.api

import java.util.UUID

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import MonitorCategoryEnum.MonitorCategoryEnum
import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.Try

/**
  * Created by zhangxu on 2017/1/10.
  */
trait ConfigService extends Service {

  /**
    * monitors
    */
  //  def getMonitorDetails: ServiceCall[NotUsed, List[MonitorDetail]]
  //
  //  def getMonitorItem(mode: String, id: Int): ServiceCall[Int, List[MonitorDetail]]

  /**
    * machine
    */
  def addMachine: ServiceCall[Machine, NotUsed]

  def getMachines: ServiceCall[NotUsed, Seq[Machine]]

  def getMachineById(id: Int): ServiceCall[NotUsed, Machine]

  def getCollectDetails: ServiceCall[NotUsed, Seq[CollectDetail]]

  def getConnectorJDBCById(id: Int): ServiceCall[NotUsed, ConnectorModeJDBC]

  def getConnectorSSHById(id: Int): ServiceCall[NotUsed, ConnectorModeSSH]

  def getMonitorSSHById(id: Int): ServiceCall[NotUsed, MonitorModeSSH]

  def getMonitorJDBCbyId(id: Int): ServiceCall[NotUsed, MonitorModeJDBC]

  def addDepositoryCollect: ServiceCall[DepositoryCollect, Done]

  def addDepositoryAnalyze: ServiceCall[DepositoryAnalyze, Done]

  def getFormatScriptById(category: String, id: Int): ServiceCall[NotUsed, FormatScript]

  //  def getMachineConnectorModeJdbc(id: Int): ServiceCall[NotUsed, ConnectorModeJDBC]


  //  def addMonitor: ServiceCall[Monitor, NotUsed]

  //  def addMonitorMode(mode: String): ServiceCall[String, NotUsed]

  //  def getMonitorList: ServiceCall[NotUsed, List[Monitor]]

  //  def getMonitorMode(mode: String, id: Int): ServiceCall[NotUsed, String]

  def addMonitorPersistence: ServiceCall[MonitorPersistence, NotUsed]

  def getMonitorPersistenceContent(id: Long, version: Long): ServiceCall[NotUsed, String]


  /**
    * alarm
    */
  def getAlarmDetails(aId: Int): ServiceCall[NotUsed, List[MonitorAlarmDetail]]

  def getAlarm(id: Int): ServiceCall[NotUsed, MonitorAlarm]

  //  def getMonitorById(id: Int): ServiceCall[NotUsed, MonitorModeJDBC]

  //  def test(id: Long, version: Long): ServiceCall[NotUsed, String]

  override final def descriptor = {
    named("oso-config").withCalls(
      //      restCall(Method.GET, "/v1/node/:id", getHost _),
      //      restCall(Method.GET, "/v1/nodes", getHosts),
      //      restCall(Method.PUT, "/v1/node/:id", putHost _),
      //      restCall(Method.DELETE, "/v1/node/:id", deleteHost _),
      //      restCall(Method.POST, "/v1/node", addHost),

      //      restCall(Method.GET, "/v1/machine/connector/jdbc/:id", getMachineConnectorModeJdbc _),

      //      restCall(Method.GET, "/v1/monitor/details", getMonitorDetails),

      //      restCall(Method.GET, "/v1/monitor/jdbc/:id", getMonitorById _),

      restCall(Method.GET, "/v1/machines", getMachines),

      restCall(Method.POST, "/v1/machine", addMachine),

      restCall(Method.GET, "/v1/machine/:id", getMachineById _),

      restCall(Method.GET, "/v1/collect/details", getCollectDetails),

      restCall(Method.GET, "/v1/connector/jdbc/:id", getConnectorJDBCById _),

      restCall(Method.GET, "/v1/connector/ssh/:id", getConnectorSSHById _),

      restCall(Method.GET, "/v1/monitor/ssh/:id", getMonitorSSHById _),

      restCall(Method.GET, "/v1/monitor/jdbc/:id", getMonitorJDBCbyId _),

      restCall(Method.POST, "/v1/depository/collect", addDepositoryCollect),

      restCall(Method.POST, "/v1/depository/analyze", addDepositoryAnalyze),

      restCall(Method.GET, "/v1/format/script/:category/:id", getFormatScriptById _),
      //      restCall(Method.POST, "/v1/monitor", addMonitor),

      //      restCall(Method.POST, "/v1/monitor/add/:mode", addMonitorMode _),


      //      restCall(Method.POST, "/v1/monitor/list", getMonitorList),

      restCall(Method.POST, "/v1/monitor/persistence", addMonitorPersistence _),

      restCall(Method.GET, "/v1/monitor/persistence/:id/:version", getMonitorPersistenceContent _),

      restCall(Method.GET, "/v1/monitor/alarm/detail/:mid", getAlarmDetails _),

      restCall(Method.GET, "/v1/monitor/alarm/:id", getAlarm _)

      //      restCall(Method.GET, "/v1/monitor/jdbc/:id", getMonitorById _)
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

case class Machine(id: Option[Int], name: String, tags: String, ip: String, state: Boolean)

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
                   tags: Seq[String],
                   label: String,
                   cron: String,
                   mode: String,
                   modeId: Int,
                   state: Boolean)

object Monitor extends ((Option[Int], Seq[String], String, String, String, Int, Boolean) => Monitor) {
  implicit val format: Format[Monitor] = Json.format[Monitor]
}

case class MonitorModeJDBC(id: Option[Int], code: String, args: Seq[String])

object MonitorModeJDBC extends ((Option[Int], String, Seq[String]) => MonitorModeJDBC) {
  implicit val format: Format[MonitorModeJDBC] = Json.format
}

case class MonitorModeSSH(id: Option[Int], code: String, args: Seq[String])

object MonitorModeSSH extends ((Option[Int], String, Seq[String]) => MonitorModeSSH) {
  implicit val format: Format[MonitorModeSSH] = Json.format
}

case class CollectDetail(id: Int, mode: String, monitorId: Int, ConnectorId: Int, cron: String, args: Seq[String])

object CollectDetail extends ((Int, String, Int, Int, String, Seq[String]) => CollectDetail) {
  implicit val format: Format[CollectDetail] = Json.format[CollectDetail]
}

case class ConnectorModeJDBC(id: Option[Int], machineId: Int, tags: Seq[String], name: String, url: String, user: String, password: String,
                             category: String, categoryVersion: String,
                             state: Boolean)

object ConnectorModeJDBC extends ((Option[Int], Int, Seq[String], String, String, String, String, String, String, Boolean) => ConnectorModeJDBC) {
  implicit val format: Format[ConnectorModeJDBC] = Json.format[ConnectorModeJDBC]
}

case class ConnectorModeSSH(id: Option[Int], machineId: Int, tags: Seq[String], name: String, port: Int,
                            user: String, password: Option[String], privateKey: Option[String],
                            category: String, categoryVersion: String,
                            state: Boolean)

object ConnectorModeSSH extends ((Option[Int], Int, Seq[String], String,
  Int, String, Option[String], Option[String], String, String, Boolean) => ConnectorModeSSH) {
  implicit val format: Format[ConnectorModeSSH] = Json.format[ConnectorModeSSH]
}

case class MonitorPersistence(id: Option[Int], stage: String, version: Long, result: String, monitorDetailId: Int)

object MonitorPersistence extends ((Option[Int], String, Long, String, Int) => MonitorPersistence) {
  implicit val format: Format[MonitorPersistence] = Json.format[MonitorPersistence]
}

case class MonitorAlarm(id: Option[Int], script: String, state: Boolean, args: String)

object MonitorAlarm extends ((Option[Int], String, Boolean, String) => MonitorAlarm) {
  implicit val format: Format[MonitorAlarm] = Json.format[MonitorAlarm]
}

case class MonitorAlarmDetail(id: Option[Int], alarmId: Int, args: Seq[String], mode: String)

object MonitorAlarmDetail extends ((Option[Int], Int, Seq[String], String) => MonitorAlarmDetail) {
  implicit val format: Format[MonitorAlarmDetail] = Json.format[MonitorAlarmDetail]
}

case class DepositoryCollect(id: Option[Int], detailId: Int, connector: String, monitor: String, data: String)

object DepositoryCollect extends ((Option[Int], Int, String, String, String) => DepositoryCollect) {
  implicit val format: Format[DepositoryCollect] = Json.format
}

case class FormatScript(id: Int, category: String, name: String, url: String)

object FormatScript extends ((Int, String, String, String) => FormatScript) {
  implicit val format: Format[FormatScript] = Json.format
}

case class DepositoryAnalyze(id: Option[Int], collectId: Int, data: String)

object DepositoryAnalyze extends ((Option[Int], Int, String) => DepositoryAnalyze) {
  implicit val format: Format[DepositoryAnalyze] = Json.format
}