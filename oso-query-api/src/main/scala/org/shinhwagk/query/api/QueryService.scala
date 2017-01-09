package org.shinhwagk.query.api

import java.sql.{DriverManager, ResultSet}

import com.lightbend.lagom.scaladsl.api.Service.restCall
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * The lagom-hello service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LagomhelloService.
  */
trait QueryService extends Service {

  def query(id: Int): ServiceCall[QueryOracleMessage, String]

  override final def descriptor = {
    import Service._
    named("oso-query").withCalls(
      restCall(Method.POST, "/api/query/oracle/:id/map", query _)
    ).withAutoAcl(true)
  }
}


case class QueryOracleMessage(jdbcUrl: String, username: String, password: String, sqlText: String, parameters: List[Any]) {
  Class.forName("oracle.jdbc.driver.OracleDriver");

  def query[T](f: (ResultSet) => List[T]) = Future {
    val conn = DriverManager.getConnection(jdbcUrl, username, password)
    val stmt = conn.prepareStatement(sqlText)
    (1 to parameters.length).foreach(num => stmt.setObject(num, parameters(num - 1)))
    val rs = stmt.executeQuery()

    val rows = f(rs)

    stmt.close()
    rs.close()
    conn.close()
    rows
  }

  def queryToMap(rs: ResultSet): List[Map[String, String]] = {
    val meta = rs.getMetaData
    val rows = new ArrayBuffer[Map[String, String]]()
    import scala.collection.mutable.Map
    while (rs.next()) {
      val row: Map[String, String] = Map.empty
      for (i <- 1 to meta.getColumnCount) {
        row += (meta.getColumnName(i) -> (if (rs.getString(i) == null) "" else rs.getString(i)))
      }
      rows += row.toMap
    }
    rows.toList
  }


  def queryToAarry(rs: ResultSet): List[List[String]] = {
    val meta = rs.getMetaData
    val rows: ArrayBuffer[List[String]] = new ArrayBuffer[List[String]]()
    rows += (1 to meta.getColumnCount).toList.map(meta.getColumnName)
    while (rs.next()) {
      rows += (1 to meta.getColumnCount).toList.map(i => (if (rs.getString(i) == null) "" else rs.getString(i)))
    }
    rows.toList
  }

}

object QueryOracleMessage {

  import play.api.libs.json._

  implicit object AnyJsonFormat extends Format[Any] {
    override def writes(o: Any): JsValue = o match {
      case i: Int => JsNumber(i)
      case s: String => JsString(s)
      case t: Boolean if t => JsBoolean(true)
      case f: Boolean if !f => JsBoolean(false)
    }

    override def reads(json: JsValue) = json match {
      case JsBoolean(true) => json.validate[Boolean]
      case JsBoolean(false) => json.validate[Boolean]
      case JsString(_) => json.validate[String]
      case JsNumber(_) => json.validate[Int]
      case _ => throw new Exception("match error")
    }
  }

  implicit val format: Format[QueryOracleMessage] = Json.format
}



