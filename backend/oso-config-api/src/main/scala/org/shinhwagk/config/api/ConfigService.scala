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
  def getMonitorDetails: ServiceCall[NotUsed, List[MonitorDetailShort]]

  def getMonitorItem(mode: String, id: Int): ServiceCall[Int, List[MonitorDetail]]

  /**
    * machine
    */
  def addMachine: ServiceCall[Machine, NotUsed]

  def getMachines: ServiceCall[NotUsed, List[Machine]]

  def addConnecter: ServiceCall[Connecter, NotUsed]

  def getConnecters: ServiceCall[NotUsed, List[Connecter]]

  def addMonitor: ServiceCall[Monitor, NotUsed]

  def addMonitorMode(mode: String): ServiceCall[String, NotUsed]

  def getMonitorList: ServiceCall[NotUsed, List[Monitor]]

  def getMonitorMode(mode: String,id:Int): ServiceCall[NotUsed, String]

  override final def descriptor = {
    named("oso-config").withCalls(
      //      restCall(Method.GET, "/v1/node/:id", getHost _),
      //      restCall(Method.GET, "/v1/nodes", getHosts),
      //      restCall(Method.PUT, "/v1/node/:id", putHost _),
      //      restCall(Method.DELETE, "/v1/node/:id", deleteHost _),
      //      restCall(Method.POST, "/v1/node", addHost),

      restCall(Method.GET, "/v1/monitor/details", getMonitorDetails),

      restCall(Method.GET, "/v1/monitor/:mode/:id", getMonitorMode _),

      restCall(Method.GET, "/v1/machines", getMachines),

      restCall(Method.POST, "/v1/machine", addMachine),

      restCall(Method.POST, "/v1/machine/connecter", addConnecter),

      restCall(Method.POST, "/v1/monitor", addMonitor),

      restCall(Method.POST, "/v1/monitor/:mode", addMonitorMode _),


      restCall(Method.POST, "/v1/monitor/list", getMonitorList)
    )
  }
}

object MonitorCategoryEnum extends Enumeration {
  type MonitorCategoryEnum = Value
  val SSH = Value("SSH")
  val JDBC = Value("JDBC")
}


case class MonitorDetailShort(id: Int, mode: String)

object MonitorDetailShort {
  implicit val format: Format[MonitorDetailShort] = Json.format[MonitorDetailShort]
}

case class MonitorDetail(id: Int, monitor_item_id: Int, machine_item_id: Int, args: List[String], mode: String, cron: String)


object MonitorDetail extends ((Int, Int, Int, List[String], String, String) => MonitorDetail) {

  implicit val format: Format[MonitorDetail] = Json.format[MonitorDetail]
}


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

case class Connecter(id: Option[Int], mheId: Int, label: String,
                     category: String, categoryVersion: String,
                     mode: String, modeInfo: String, state: Boolean)

object Connecter extends ((Option[Int], Int, String, String, String, String, String, Boolean) => Connecter) {
  implicit val format: Format[Connecter] = Json.format[Connecter]
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
