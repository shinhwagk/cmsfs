package org.shinhwagk.config.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.config.api._
import org.shinhwagk.config.db.Tables
import play.api.libs.json.Json
import slick.driver.MySQLDriver.api._
import slick.jdbc.GetResult

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of the LagomhelloService.
  */
class ConfigServiceImpl(configService: ConfigService)(implicit ec: ExecutionContext) extends ConfigService {

  val db = Database.forConfig("oso-config")

  /**
    *
    * monitors
    *
    */
  override def getMonitorDetails: ServiceCall[NotUsed, List[MonitorDetailShort]] = ServiceCall { _ =>
    implicit val getMonitorDetailShort = GetResult(r => MonitorDetailShort(r.<<, r.<<))
    db.run(sql"SELECT monitor_item_detail.id, monitor_item_detail.monitor_mode FROM monitor_item_detail".as[MonitorDetailShort])
      .map(_.toList)
  }

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
  override def getMonitorItem(mode: String, id: Int): ServiceCall[Int, List[MonitorDetail]] = ???

  /**
    * machine
    */
  override def getMachines: ServiceCall[NotUsed, List[Machine]] = ServiceCall { _ =>
    db.run(Tables.machines.result).map(_.toList)
  }

  override def addMachine: ServiceCall[Machine, NotUsed] = ServiceCall { machine =>
    db.run(Tables.machines += machine).map(_ => NotUsed)
  }

  override def getConnecters: ServiceCall[NotUsed, List[Connecter]] = ServiceCall { _ =>
    db.run(Tables.connecters.result).map(_.toList)
  }

  override def addConnecter: ServiceCall[Connecter, NotUsed] = ServiceCall { connecter =>
    db.run(Tables.connecters += connecter).map(_ => NotUsed)
  }

  override def addMonitor: ServiceCall[Monitor, NotUsed] = ServiceCall { monitor =>
    db.run(Tables.monitors += monitor).map(_ => NotUsed)
  }

  override def addMonitorMode(mode: String): ServiceCall[String, NotUsed] = ServiceCall { string =>

    mode match {
      case "JDBC" => db.run(Tables.monitorModeJdbcs += Json.parse(string).as[MonitorModeJDBC]).map(_ => NotUsed)
      //      case "SSH" =>db.run(Tables.monitorModeSsh += monitorMode).map(_ => NotUsed)
    }

  }

  override def getMonitorList: ServiceCall[NotUsed, List[Monitor]] = ServiceCall { _ =>
    db.run(Tables.monitors.result).map(_.toList)
  }

  override def getMonitorMode(mode: String, id: Int): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    mode match {
      case "JDBC" => db.run(Tables.monitorModeJdbcs.filter(_.id === id).result.head).map(Json.toJson(_).toString())
      case "SSH" => db.run(Tables.monitorModeSSHs.filter(_.id === id).result.head).map(Json.toJson(_).toString())
    }
  }
}
