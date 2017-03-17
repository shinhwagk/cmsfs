package org.wex.cmsfs.config.db

import org.wex.cmsfs.config.api.MonitorCategoryEnum.MonitorCategoryEnum
import org.wex.cmsfs.config.api.{MonitorAlarm, MonitorAlarmDetail, MonitorCategoryEnum}
import org.wex.cmsfs.config.db.table.{CoreFormatAlarms, _}
import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

object Tables {

  private implicit val categoryMapper = MappedColumnType.base[MonitorCategoryEnum, String](
    _ match {
      case MonitorCategoryEnum.JDBC => "ORACLE"
      case MonitorCategoryEnum.SSH => "OS"
    },
    _ match {
      case "ORACLE" => MonitorCategoryEnum.JDBC
      case "OS " => MonitorCategoryEnum.SSH
    }
  )

  //  implicit object AnyJsonFormat extends Format[Any] {
  //    override def reads(json: JsValue): JsResult[Any] = json match {
  //      case JsBoolean(true) => json.validate[Boolean]
  //      case JsBoolean(false) => json.validate[Boolean]
  //      case JsString(_) => json.validate[String]
  //      case JsNumber(_) => json.validate[Int]
  //      case _ => throw new Exception("match error")
  //    }
  //
  //    override def writes(o: Any): JsValue = o match {
  //      case i: Int => JsNumber(i)
  //      case s: String => JsString(s)
  //      case t: Boolean if t => JsBoolean(true)
  //      case f: Boolean if !f => JsBoolean(false)
  //    }
  //  }
  //
  //  private implicit val listAnyMapper = MappedColumnType.base[List[Any], String](
  //    Json.toJson(_).toString(),
  //    Json.parse(_).as[List[Any]]
  //  )
  //
  //  private implicit val tagsMapper = MappedColumnType.base[StringList, String](
  //    Json.toJson(_).toString(),
  //    Json.parse(_).as[StringList]
  //  )
  //
  //
  implicit val tagsMapper2 = MappedColumnType.base[Seq[String], String](
    Json.toJson(_).toString(),
    Json.parse(_).as[Seq[String]]
  )

  val metrics = TableQuery[Metrics]

  val monitorModeSSHs = TableQuery[MonitorModeSSHs]

  val machines = TableQuery[Machines]

  val connectorModeJDBCs = TableQuery[ConnectorModeJdbcs]

  val connectorModeSSHs = TableQuery[ConnectorModeSSHs]

  val monitorDetails = TableQuery[MonitorDetails]

  val depositoryCollects = TableQuery[MonitorDepositories]

  val depositoryAnalyzes = TableQuery[DepositoryAnalyzes]

  val formatScripts = TableQuery[FormatScripts]

  val coreMonitorDetails = TableQuery[CoreMonitorDetails]
  val coreCollectorJdbcs = TableQuery[CoreConnectorJdbcs]
  val coreCollectorSshs = TableQuery[CoreConnectorSshs]
  val coreCollects = TableQuery[CoreCollects]
  val coreFormatAlarms = TableQuery[CoreFormatAlarms]
  val coreFormatAnalyzes = TableQuery[CoreFormatAnalyzes]

  //  val monitorDetails = TableQuery[MonitorDetails]

  case class MonitorModeJDBC(id: Option[Int], category: String, categoryVersion: Seq[String], code: String, args: List[Any])

  class MonitorAlarmDetails(tag: Tag) extends Table[MonitorAlarmDetail](tag, "alarm_details") {
    def id = column[Option[Int]]("ID")

    def alarmId = column[Int]("ALARM_ID")

    def args = column[Seq[String]]("ARGS")

    def mode = column[String]("MODE")

    override def * = (id, alarmId, args, mode) <> (MonitorAlarmDetail.tupled, MonitorAlarmDetail.unapply)
  }

  val monitorAlarmDetails = TableQuery[MonitorAlarmDetails]

  class MonitorAlarms(tag: Tag) extends Table[MonitorAlarm](tag, "alarms") {
    def id = column[Option[Int]]("ID")

    def script = column[String]("SCRIPT")

    def state = column[Boolean]("STATE")

    def args = column[String]("ARGS")

    override def * = (id, script, state, args) <> (MonitorAlarm.tupled, MonitorAlarm.unapply)
  }

  val monitorAlarms = TableQuery[MonitorAlarms]
}
