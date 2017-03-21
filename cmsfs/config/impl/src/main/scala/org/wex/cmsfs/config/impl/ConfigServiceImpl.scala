package org.wex.cmsfs.config.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.wex.cmsfs.config.api._
import org.wex.cmsfs.config.db.Tables
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext

class ConfigServiceImpl()(implicit ec: ExecutionContext) extends ConfigService {

  val db = Database.forConfig("oso-config")

  /**
    *
    * monitors
    *
    */
  //  override def getMonitorDetails: ServiceCall[NotUsed, List[MonitorDetail]] = ServiceCall { _ =>
  //    db.run(Tables.monitorDetails.result).map(_.toList)
  //  }

  //  override def getMonitorItem(item: String, id: Int): ServiceCall[NotUsed, List[MonitorDetail]] = ServiceCall { _ =>
  //
  ////    item match {
  ////      case MonitorCategoryEnum.JDBC =>
  ////        db.run()
  ////      case MonitorCategoryEnum.SSH =>
  ////
  ////    }
  //  }
  //  override def getMonitorItem(mode: String, id: Int): ServiceCall[Int, List[MonitorDetail]] = ServiceCall { _ =>
  //    implicit val getMonitorDetailShort = GetResult(r => MonitorDetail(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
  //    db.run(sql"SELECT monitor_item_detail.id, monitor_item_detail.monitor_mode FROM monitor_detail".as[MonitorDetailShort])
  //      .map(_.toList)
  //
  //  }
  //  override def getMonitorItem(mode: String, id: Int): ServiceCall[Int, List[MonitorDetail]] = ???

  /**
    * machine
    */
  override def getMachines: ServiceCall[NotUsed, Seq[Machine]] = ServiceCall { _ =>
    db.run(Tables.machines.result)
  }

  override def addMachine: ServiceCall[Machine, NotUsed] = ServiceCall { machine =>
    db.run(Tables.machines += machine).map(_ => NotUsed)
  }

  //  override def addMonitor: ServiceCall[Monitor, NotUsed] = ServiceCall { monitor =>
  //    db.run(Tables.monitors += monitor).map(_ => NotUsed)
  //  }

  //  override def addMonitorMode(mode: String): ServiceCall[String, NotUsed] = ServiceCall { string =>
  //
  //    mode match {
  //      case "JDBC" => db.run(Tables.monitorModeJdbcs += Json.parse(string).as[MonitorModeJDBC]).map(_ => NotUsed)
  //      //      case "SSH" =>db.run(Tables.monitorModeSsh += monitorMode).map(_ => NotUsed)
  //    }
  //
  //  }

  //  override def getMonitorList: ServiceCall[NotUsed, List[Monitor]] = ServiceCall { _ =>
  //    db.run(Tables.monitors.result).map(_.toList)
  //  }

  //  override def getMonitorMode(mode: String, id: Int): ServiceCall[NotUsed, String] = ServiceCall { _ =>
  //    mode match {
  //      case "JDBC" => db.run(Tables.monitorModeJdbcs.filter(_.id === id).result.head).map(Json.toJson(_).toString())
  //      //      case "SSH" => db.run(Tables.monitorModeSSHs.filter(_.id === id).result.head).map(Json.toJson(_).toString())
  //    }
  //  }

  //  override def getMachineConnectorModeJdbc(id: Int): ServiceCall[NotUsed, ConnectorModeJDBC] = ServiceCall { _ =>
  //    db.run(Tables.machineConnectorModeJdbcs.filter(_.id === id).result.head)
  //  }


  //  override def getAlarmDetails(aId: Int): ServiceCall[NotUsed, List[MonitorAlarmDetail]] = ServiceCall { _ =>
  //    db.run(Tables.monitorAlarmDetails.filter(_.alarmId === aId).result).map(_.toList)
  //  }


  //  override def getMonitorById(id: Int): ServiceCall[NotUsed, api.MonitorModeJDBC] = ServiceCall { _ =>
  //    db.run(Tables.monitorModeJDBCs.filter(_.id === id).result.head).map(f = mmj => {
  //      api.MonitorModeJDBC(mmj.id.get, mmj.category, JsonFormat.toJsArray(mmj.categoryVerison), mmj.dslCode, mmj.args.map(_.toString).toSeq)
  //    })
  //  }
  override def getMonitorDetails: ServiceCall[NotUsed, Seq[MonitorDetail]] = ServiceCall { _ =>
    db.run(Tables.monitorDetails.result)
  }

  override def getConnectorSSHById(id: Int): ServiceCall[NotUsed, ConnectorModeSSH] = ServiceCall { _ =>
    db.run(Tables.connectorModeSSHs.filter(_.id === id).result.head)
  }

  override def getConnectorJDBCById(id: Int): ServiceCall[NotUsed, ConnectorModeJDBC] = ServiceCall { _ =>
    db.run(Tables.connectorModeJDBCs.filter(_.id === id).result.head)
  }

  override def getMetricById(id: Int): ServiceCall[NotUsed, Metric] = ServiceCall { _ =>
    db.run(Tables.metrics.filter(_.id === id).result.head)
  }

  override def getMachineById(id: Int): ServiceCall[NotUsed, Machine] = ServiceCall { _ =>
    db.run(Tables.machines.filter(_.id === id).result.head)
  }

  override def addMonitorDepository: ServiceCall[MonitorDepository, Long] = ServiceCall { dc =>
    val table = Tables.depositoryCollects
    val userId = (table returning table.map(_.id)) += dc
    db.run(userId).map(_.get)
  }

  override def getFormatScriptById(category: String, id: Int): ServiceCall[NotUsed, FormatScript] = ServiceCall { _ =>
    db.run(Tables.formatScripts.filter(_.id === id).filter(_.category === category).result.head)
  }

  override def getMonitorDepositoryById(id: Long): ServiceCall[NotUsed, MonitorDepository] = ServiceCall { _ =>
    db.run(Tables.depositoryCollects.filter(_.id === id).result.head)
  }

  override def getCoreMonitorDetails: ServiceCall[NotUsed, Seq[CoreMonitorDetail]] = ServiceCall { _ =>
    db.run(Tables.coreMonitorDetails.result)
  }

  override def getCoreConnectorJdbcById(id: Int): ServiceCall[NotUsed, CoreConnectorJdbc] = ServiceCall { _ =>
    db.run(Tables.coreCollectorJdbcs.filter(_.id === id).result.head)
  }

  override def getCoreConnectorSshById(id: Int): ServiceCall[NotUsed, CoreConnectorSsh] = ServiceCall { _ =>
    db.run(Tables.coreCollectorSshs.filter(_.id === id).result.head)
  }

  override def getCoreCollectById(id: Int): ServiceCall[NotUsed, CoreCollect] = ServiceCall { _ =>
    db.run(Tables.coreCollects.filter(_.id === id).result.head)
  }

  override def getCoreFormatAnalyzesById(id: Int): ServiceCall[NotUsed, CoreFormatAnalyze] = ServiceCall { _ =>
    db.run(Tables.coreFormatAnalyzes.filter(_.id === id).result.head)
  }

  override def getCoreFormatAnalyzesByCollectId(id: Int): ServiceCall[NotUsed, Seq[CoreFormatAnalyze]] = ServiceCall { _ =>
    db.run(Tables.coreFormatAnalyzes.filter(_.collectId === id).result)
  }

  override def getCoreFormatAlarmsById(id: Int): ServiceCall[NotUsed, CoreFormatAlarm] = ServiceCall { _ =>
    db.run(Tables.coreFormatAlarms.filter(_.id === id).result.head)
  }

  override def addCoreMonitorDetail: ServiceCall[CoreMonitorDetail, Done] = ServiceCall { cmd =>
    db.run(Tables.coreMonitorDetails += cmd).map(_ => Done)
  }

  override def addCoreConnectorJdbc: ServiceCall[CoreConnectorJdbc, Done] = ServiceCall { ccj =>
    db.run(Tables.coreCollectorJdbcs += ccj).map(_ => Done)
  }

  override def addCoreConnectorSsh: ServiceCall[CoreConnectorSsh, Done] = ServiceCall { ccs =>
    db.run(Tables.coreCollectorSshs += ccs).map(_ => Done)
  }

  override def addCoreCollect: ServiceCall[CoreCollect, Done] = ServiceCall { cc =>
    db.run(Tables.coreCollects += cc).map(_ => Done)
  }

  override def addCoreFormatAnalyze: ServiceCall[CoreFormatAnalyze, Done] = ServiceCall { cfa =>
    db.run(Tables.coreFormatAnalyzes += cfa).map(_ => Done)
  }

  override def addCoreFormatAlarm: ServiceCall[CoreFormatAlarm, Done] = ServiceCall { cfa =>
    db.run(Tables.coreFormatAlarms += cfa).map(_ => Done)
  }

}