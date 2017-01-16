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

  override def getHost(id: Int): ServiceCall[NotUsed, Host] = ServiceCall { _ =>
    db.run(Tables.hosts.filter(_.id === id).result.head)
  }

  override def getHosts: ServiceCall[NotUsed, List[Host]] = ServiceCall { _ =>
    db.run(Tables.hosts.result).map(_.toList)
  }

  override def deleteHost(id: Int): ServiceCall[NotUsed, NotUsed] = ServiceCall { _ =>
    db.run(Tables.hosts.filter(_.id === id).delete).map(_ => NotUsed)
  }

  override def addHost: ServiceCall[Host, NotUsed] = ServiceCall { p =>
    db.run(Tables.hosts += p).map(_ => NotUsed)
  }

  override def putHost(id: Int): ServiceCall[Host, NotUsed] = ServiceCall { p =>
    db.run(Tables.hosts.filter(_.id === id).update(p.copy(id = Some(id)))).map(_ => NotUsed)
  }

  /**
    *
    * monitors
    *
    */
  override def getMonitorDetails: ServiceCall[NotUsed, List[MonitorDetailShort]] = ServiceCall { _ =>
    implicit val getMonitorDetailShort = GetResult(r => MonitorDetailShort(r.<<, r.<<))
    db.run(sql"SELECT monitor_item_detail.id, monitor_item_detail.monitor_mode FROM monitor_item_detail".as[MonitorDetailShort])
      .map(_.toList)
    //      .map(_.map(row => MonitorDetailShort(row._1, row._2)).toList)
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

}