package org.wex.cmsfs.config.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.wex.cmsfs.config.api._
import org.wex.cmsfs.config.db.Tables
import play.api.libs.json.Json
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class ConfigServiceImpl()(implicit ec: ExecutionContext) extends ConfigService {

  val db = Database.forConfig("cmsfs-config")

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

  override def getCoreMonitorDetailById(id: Int): ServiceCall[NotUsed, CoreMonitorDetail] = ServiceCall { _ =>
    db.run(Tables.coreMonitorDetails.filter(_.id === id).result.head)
  }

  override def addCoreMonitorStatus: ServiceCall[CoreMonitorStatus, Done] = ServiceCall { cms =>
    val action = Tables.coreMonitorStatuses.filter(_.id === cms.id).exists.result.flatMap { exists =>
      if (!exists) {
        Tables.coreMonitorStatuses += cms
      } else {
        DBIO.successful(None)
        //        Tables.coreMonitorStatuses.filter(_.id === cms.id).map(p => (p.category, p.metric)).update(cms.category, cms.metric)
      }
    }
    db.run(action).map(_ => Done)
  }

  override def putCoreMonitorStatusCollectById(id: Int): ServiceCall[List[String], Done] = ServiceCall { lis =>
    val c = db.run(Tables.coreMonitorStatuses.filter(_.id === id).map(_.status).result.head)
    c.flatMap { p =>
      val np = Json.parse(p).as[Map[String, List[String]]].+("COLLECT" -> lis)
      db.run(Tables.coreMonitorStatuses.filter(_.id === id).map(_.status).update(Json.toJson(np).toString()))
    }.map(_ => Done)
  }

  override def putCoreMonitorStatusAnalyzeById(id: Int): ServiceCall[List[String], Done] = ServiceCall { lis =>
    val c = db.run(Tables.coreMonitorStatuses.filter(_.id === id).map(_.status).result.head)
    c.flatMap { p =>
      val np = Json.parse(p).as[Map[String, List[String]]].+("ANALYZE" -> lis)
      db.run(Tables.coreMonitorStatuses.filter(_.id === id).map(_.status).update(Json.toJson(np).toString()))
    }.map(_ => Done)
  }

  override def putCoreMonitorStatusAlarmById(id: Int): ServiceCall[List[String], Done] = ServiceCall { lis =>
    val c = db.run(Tables.coreMonitorStatuses.filter(_.id === id).map(_.status).result.head)
    c.flatMap { p =>
      val np = Json.parse(p).as[Map[String, List[String]]].+("ALARM" -> lis)
      db.run(Tables.coreMonitorStatuses.filter(_.id === id).map(_.status).update(Json.toJson(np).toString()))
    }.map(_ => Done)
  }
}