package org.shinhwagk.config.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json._

/**
  * Created by zhangxu on 2017/1/10.
  */
trait ConfigService extends Service {

  def getNode(id: Int): ServiceCall[NotUsed, Host]

  def getNodes: ServiceCall[NotUsed, List[Host]]

  def deleteNode(id: Int): ServiceCall[NotUsed, NotUsed]

  def addNode: ServiceCall[Host, NotUsed]

  def putNode(id: Int): ServiceCall[Host, NotUsed]

  override final def descriptor = {
    named("oso-config").withCalls(
      restCall(Method.GET, "/v1/node/:id", getNode _),
      restCall(Method.GET, "/v1/nodes", getNodes),
      restCall(Method.PUT, "/v1/node/:id", putNode _),
      restCall(Method.DELETE, "/v1/node/:id", deleteNode _),
      restCall(Method.POST, "/v1/node", addNode)
    )
  }
}

case class Host(id: Option[Int], label: String, hostname: String, ip: String, port: Int, tags: List[String], status: Boolean)

object Host extends ((Option[Int], String, String, String, Int, List[String], Boolean) => Host) {
  implicit val format: Format[Host] = Json.format[Host]
}

//case class RDatabase(id: Int, hid: Int, jdbcUrl: String, user: String, password: String, status: Boolean, tag: List[String])
//
//object RDatabase {
//  implicit val format: Format[RDatabase] = Json.format[RDatabase]
//}