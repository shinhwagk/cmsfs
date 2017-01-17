package org.shinhwagk.config.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.config.api._
import org.shinhwagk.config.db.Tables
import slick.driver.MySQLDriver.api._
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext

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
}
