package api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall, _}
import com.lightbend.lagom.scaladsl.api.transport.Method
import play.api.libs.json._

/**
  * Created by zhangxu on 2017/1/10.
  */
trait ConfigService extends Service {

  def getNodes: ServiceCall[NotUsed, List[Node]]

  def getNode(id: Int): ServiceCall[NotUsed, Node]

  def getRDatabase(id: Int): ServiceCall[NotUsed, RDatabase]

  override final def descriptor = {
    named("oso-config").withCalls(
      restCall(Method.GET, "/v1/nodes", getNodes _),
      restCall(Method.GET, "/v1/node/:id", getNode _),
      restCall(Method.GET, "/v1/node/rdb/:id", getRDatabase _)
    ).withAutoAcl(true)
  }
}

case class Node(id: Int, hostname: String, ip: String, port: Int, tag: List[String], status: Boolean)

object Node {
  implicit val format: Format[Node] = Json.format[Node]
}

case class RDatabase(id: Int, hid: Int, jdbcUrl: String, user: String, password: String, status: Boolean, tag: List[String])

object RDatabase {
  implicit val format: Format[RDatabase] = Json.format[RDatabase]
}