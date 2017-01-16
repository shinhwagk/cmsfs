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

  def getHost(id: Int): ServiceCall[NotUsed, Host]

  def getHosts: ServiceCall[NotUsed, List[Host]]

  def deleteHost(id: Int): ServiceCall[NotUsed, NotUsed]

  def addHost: ServiceCall[Host, NotUsed]

  def putHost(id: Int): ServiceCall[Host, NotUsed]

  /**
    * monitors
    */
  def getMonitorDetails: ServiceCall[NotUsed, List[MonitorDetailShort]]

  //  def getMonitorItem(item: String, id: Int): ServiceCall[Int, List[MonitorDetail]]

  override final def descriptor = {
    named("oso-config").withCalls(
      restCall(Method.GET, "/v1/node/:id", getHost _),
      restCall(Method.GET, "/v1/nodes", getHosts),
      restCall(Method.PUT, "/v1/node/:id", putHost _),
      restCall(Method.DELETE, "/v1/node/:id", deleteHost _),
      restCall(Method.POST, "/v1/node", addHost),

      restCall(Method.GET, "/v1/monitor/details", getMonitorDetails)
      //      restCall(Method.GET, "/v1/monitor/:item/:id", getMonitorItem _)
    )
  }
}

case class Host(id: Option[Int], label: String, hostname: String, ip: String, port: Int, tags: List[String], status: Boolean)

object Host extends ((Option[Int], String, String, String, Int, List[String], Boolean) => Host) {
  implicit val format: Format[Host] = Json.format[Host]
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