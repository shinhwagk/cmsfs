package org.shinhwagk.query.api

import java.io.{BufferedReader, InputStreamReader}
import java.sql.{DriverManager, ResultSet}

import com.jcraft.jsch.{ChannelExec, JSch}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Json, _}

import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait QueryService extends Service {

  def queryForOracle(mode: String): ServiceCall[QueryOracleMessage, String]

  def queryForOSScript: ServiceCall[QueryOSMessage, String]

  override final def descriptor = {
    import Service._
    named("query").withCalls(
      restCall(Method.POST, "/api/query/oracle/:mode", queryForOracle _),
      restCall(Method.POST, "/api/query/os", queryForOSScript _)
    ).withAutoAcl(true)
  }
}

object QueryModeEnum extends Enumeration {
  type QueryMode = Value
  val ARRAY = Value("ARRAY")
  val MAP = Value("MAP")
}

case class QueryOSMessage(host: String, user: String, scriptUrl: String, port: Option[Int] = Some(22)) {
  def exec: Future[String] = Future {
    val OSName = System.getProperty("os.name").toLowerCase();
    if (OSName.startsWith("win")) {
      ssh("C:\\Users\\zhangxu\\.ssh\\id_rsa", user, host, scriptUrl, port.get)
    } else if (OSName == "linux") {
      ssh("~/.ssh/id_rsa", user, host, scriptUrl, port.get)
    } else {
      throw new Exception("OS not match..")
    }
  }

  def ssh(keyPath: String, user: String, host: String, scriptUrl: String, port: Int): String = {
    val jsch = new JSch();
    jsch.addIdentity(keyPath);
    val session = jsch.getSession(user, host, port);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    val channelExec: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    val in = channelExec.getInputStream();
    channelExec.setCommand(s"curl -s ${scriptUrl} | sh");

    channelExec.connect();

    val reader = new BufferedReader(new InputStreamReader(in));

    val rs = new ArrayBuffer[String]()

    var line: Option[String] = Option(reader.readLine())

    while (line.isDefined) {
      rs += line.get
      line = Option(reader.readLine())
    }

    val exitStatus: Int = channelExec.getExitStatus();

    channelExec.disconnect();
    session.disconnect();

    if (exitStatus < 0) {
      // System.out.println("Done, but exit status not set!");
    } else if (exitStatus > 0) {
      // System.out.println("Done, but with error!");
    } else {
      // System.out.println("Done!");
    }
    Json.toJson(rs).toString()
  }
}

object QueryOSMessage {
  implicit val format: Format[QueryOSMessage] = Json.format[QueryOSMessage]
}

case class QueryOracleMessage(jdbcUrl: String, username: String, password: String, sqlText: String, parameters: Seq[String]) {
  Class.forName("oracle.jdbc.driver.OracleDriver");

  def mode(mode: String): Future[String] = {
    QueryModeEnum.withName(mode.toUpperCase) match {
      case QueryModeEnum.ARRAY => query[List[String]](queryToAarry)
      case QueryModeEnum.MAP => query[Map[String, String]](queryToMap)
    }
  }

  def query[T](f: (ResultSet) => JsValue) = Future {
    val conn = DriverManager.getConnection(jdbcUrl, username, password)
    val stmt = conn.prepareStatement(sqlText)
    (1 to parameters.length).foreach(num => stmt.setObject(num, 5))
    val rs = stmt.executeQuery()

    val rows = f(rs)

    stmt.close()
    rs.close()
    conn.close()

    rows.toString()
  }

  def queryToMap(rs: ResultSet): JsValue = {
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
    println(rows.toList)
    Json.toJson(rows.toList)
  }


  def queryToAarry(rs: ResultSet): JsValue = {
    val meta = rs.getMetaData
    val rows: ArrayBuffer[List[String]] = new ArrayBuffer[List[String]]()
    rows += (1 to meta.getColumnCount).toList.map(meta.getColumnName)
    while (rs.next()) {
      rows += (1 to meta.getColumnCount).toList.map(i => (if (rs.getString(i) == null) "" else rs.getString(i)))
    }
    Json.toJson(rows.toList)
  }

}

object QueryOracleMessage extends ((String, String, String, String, Seq[String]) => QueryOracleMessage) {

  //  implicit object AnyJsonFormat extends Format[Any] {
  //    override def writes(o: Any): JsValue = o match {
  //      case i: Int => JsNumber(i)
  //      case i: Long => JsNumber(i)
  //      case s: String => JsString(s)
  //      case t: Boolean if t => JsBoolean(true)
  //      case f: Boolean if !f => JsBoolean(false)
  //      case _ => throw new Exception("match error _" + o)
  //    }
  //
  //    override def reads(json: JsValue) = json match {
  //      case JsBoolean(true) => json.validate[Boolean]
  //      case JsBoolean(false) => json.validate[Boolean]
  //      case JsString(_) => json.validate[String]
  //      case JsNumber(_) => json.validate[Int]
  //      case _ => throw new Exception("match error")
  //    }
  //  }

  implicit val format: Format[QueryOracleMessage] = Json.format[QueryOracleMessage]
}



