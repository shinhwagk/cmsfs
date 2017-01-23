package org.shinhwagk.config.db

import org.shinhwagk.config.api.MonitorCategoryEnum.MonitorCategoryEnum
import org.shinhwagk.config.api.{MonitorAlarm, _}
import play.api.libs.json._
import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

/**
  * Created by zhangxu on 2017/1/10.
  */
object Tables {

  private type StringList = List[String]

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

  private implicit val listAnyMapper = MappedColumnType.base[List[Any], String](
    Json.toJson(_).toString(),
    Json.parse(_).as[List[Any]]
  )

  private implicit val tagsMapper = MappedColumnType.base[StringList, String](
    Json.toJson(_).toString(),
    Json.parse(_).as[StringList]
  )

  //  class Monitors(tag: Tag) extends Table[Monitor](tag, "monitors") {
  //    def id = column[Option[Int]]("SUP_ID", O.PrimaryKey, O.AutoInc)
  //
  //    def category = column[MonitorCategoryEnum]("category")
  //
  //    def label = column[String]("label")
  //
  //    def args = column[StringList]("args")
  //
  //    def tags = column[StringList]("tags")
  //
  //    def state = column[Boolean]("state")
  //
  //    def * = (id, category, label, args, tags, state) <> (Monitor.tupled, Monitor.unapply)
  //  }

  //  class Machines(tag: Tag) extends Table[Host](tag, "host") {
  //    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
  //
  //    def label = column[String]("LABEL")
  //
  //    def hostname = column[String]("HOSTNAME")
  //
  //    def ip = column[String]("IP")
  //
  //    def port = column[Int]("PORT")
  //
  //    def tags = column[StringList]("TAGS")
  //
  //    def status = column[Boolean]("STATUS")
  //
  //    override def * = (id, label, hostname, ip, port, tags, status) <> (Host.tupled, Host.unapply)
  //  }

  //  val hosts = TableQuery[Machines]

  //  class MonitorDetails(tag: Tag) extends Table[MonitorDetail](tag, "monitor_item_details") {
  //    def id = column[Int]("MONITOR_DETAIL_ID", O.PrimaryKey, O.AutoInc)
  //
  //    def monitor_item_id = column[Int]("MONITOR_ITEM_ID")
  //
  //    def machine_item_id = column[Int]("MACHINE_ITEM_ID")
  //
  //    def args = column[List[String]]("ARGS")
  //
  //    def cron = column[String]("CRON")
  //
  //    def mode = column[String]("MODE")
  //
  //    override def * = (id, monitor_item_id, machine_item_id, args, cron, mode) <> (MonitorDetail.tupled, MonitorDetail.unapply)
  //  }
  //
  //  val monitorDetails = TableQuery[MonitorDetails]

  class Machines(tag: Tag) extends Table[Machine](tag, "machines") {
    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def label = column[String]("LABEL")

    def ip = column[String]("IP")

    def state = column[Boolean]("STATE")

    override def * = (id, name, label, ip, state) <> (Machine.tupled, Machine.unapply)
  }

  val machines = TableQuery[Machines]

  class Connectors(tag: Tag) extends Table[Connector](tag, "connectors") {
    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

    def mheId = column[Int]("MACHINE_ID")

    def label = column[String]("LABEL")

    def category = column[String]("CATEGORY")

    def categoryVersion = column[String]("CATEGORY_VERSION")

    def mode = column[String]("MODE")

    def modeInfo = column[String]("MODE_INFO")

    def state = column[Boolean]("STATE")

    override def * = (id, mheId, label, category, categoryVersion, mode, modeInfo, state) <> (Connector.tupled, Connector.unapply)
  }

  val connectors = TableQuery[Connectors]

  class Monitors(tag: Tag) extends Table[Monitor](tag, "monitors") {

    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)

    def label = column[String]("LABEL")

    def name = column[String]("NAME")

    def cron = column[String]("CRON")

    def mode = column[String]("MODE")

    def modeId = column[Int]("MODE_ID")

    def persistence = column[Boolean]("PERSISTENCE")

    def state = column[Boolean]("STATE")

    override def * = (id, label, name, cron, mode, modeId, persistence, state) <> (Monitor.tupled, Monitor.unapply)
  }

  val monitors = TableQuery[Monitors]

  class MonitorModeJDBCs(tag: Tag) extends Table[MonitorModeJDBC](tag, "monitor_mode_jdbc") {
    def id = column[Option[Int]]("ID")

    def category = column[String]("Category")

    def categoryVersion = column[String]("CATEGORY_VERSION")

    def code = column[String]("CODE")

    override def * = (id, category, categoryVersion, code) <> (MonitorModeJDBC.tupled, MonitorModeJDBC.unapply)
  }

  val monitorModeJdbcs = TableQuery[MonitorModeJDBCs]

  class MonitorModeSSHs(tag: Tag) extends Table[MonitorModeSSH](tag, "monitor_mode_ssh") {
    def id = column[Option[Int]]("ID")

    def category = column[String]("Category")

    def categoryVersion = column[String]("CATEGORY_VERSION")

    def code = column[String]("CODE")

    override def * = (id, category, categoryVersion, code) <> (MonitorModeSSH.tupled, MonitorModeSSH.unapply)
  }

  val monitorModeSSHs = TableQuery[MonitorModeSSHs]

  class MonitorDetails(tag: Tag) extends Table[MonitorDetail](tag, "monitor_details") {
    def id = column[Option[Int]]("ID")

    def mode = column[String]("MODE")

    def monitorModeId = column[Int]("MONITOR_MODE_ID")

    def machineConnectorId = column[Int]("MACHINE_CONNECTOR_MODE_ID")

    def cron = column[String]("CRON")

    def persistence = column[Boolean]("PERSISTENCE")

    def alarm = column[Boolean]("ALARM")

    def chart = column[Boolean]("CHART")

    override def * = (id, mode, monitorModeId, machineConnectorId, cron, persistence, alarm, chart) <> (MonitorDetail.tupled, MonitorDetail.unapply)
  }

  val monitorDetails = TableQuery[MonitorDetails]

  class MachineConnectorModeJdbcs(tag: Tag) extends Table[MachineConnectorModeJDBC](tag, "connectors_mode_jdbc") {
    def id = column[Option[Int]]("ID")

    def category = column[String]("Category")

    def categoryVersion = column[String]("CATEGORY_VERSION")

    def connectorId = column[Int]("CONNECTOR_ID")

    def jdbcUrl = column[String]("JDBC_URL")

    def username = column[String]("USERNAME")

    def password = column[String]("PASSWORD")

    override def * = (id, category, categoryVersion, connectorId, jdbcUrl, username, password) <> (MachineConnectorModeJDBC.tupled, MachineConnectorModeJDBC.unapply)
  }

  val machineConnectorModeJdbcs = TableQuery[MachineConnectorModeJdbcs]

  class MachineConnectorModeSSHs(tag: Tag) extends Table[MachineConnectorModeSSH](tag, "connectors_mode_ssh") {
    def id = column[Option[Int]]("ID")

    def category = column[String]("Category")

    def categoryVersion = column[String]("CATEGORY_VERSION")

    def connectorId = column[Int]("CONNECTOR_ID")

    def sshPort = column[Int]("ssh_port")

    def user = column[String]("USER")

    def password = column[String]("PASSWORD")

    def privateKey = column[String]("PRIVATE_KEY")

    override def * = (id, category, categoryVersion, connectorId, sshPort, user, password, privateKey) <> (MachineConnectorModeSSH.tupled, MachineConnectorModeSSH.unapply)
  }

  val machineConnectorModeSSHs = TableQuery[MachineConnectorModeSSHs]

  class MonitorPersistences(tag: Tag) extends Table[MonitorPersistence](tag, "monitor_persistence") {
    def id = column[Option[Int]]("ID")

    def stage = column[String]("STAGE")

    def version = column[Long]("VERSION")

    def result = column[String]("RESULT")

    def monitorDetailId = column[Int]("MONITOR_DETAIL_ID")

    override def * = (id, stage, version, result, monitorDetailId) <> (MonitorPersistence.tupled, MonitorPersistence.unapply)
  }

  val monitorPersistences = TableQuery[MonitorPersistences]


  class MonitorAlarmDetails(tag: Tag) extends Table[MonitorAlarmDetail](tag, "alarm_details") {
    def id = column[Option[Int]]("ID")

    def alarmId = column[Int]("ALARM_ID")

    def args = column[List[String]]("ARGS")

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
