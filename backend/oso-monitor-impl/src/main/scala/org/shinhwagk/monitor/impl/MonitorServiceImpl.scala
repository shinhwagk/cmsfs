package org.shinhwagk.config.impl

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.config.api.ConfigService
import org.shinhwagk.monitor.api.MonitorService
import org.shinhwagk.monitor.monitor.MonitorSlave
import org.shinhwagk.query.api.{QueryOracleMessage, QueryService}
import slick.driver.MySQLDriver.api._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Implementation of the LagomhelloService.
  */
class MonitorServiceImpl(configService: ConfigService, queryService: QueryService)(implicit ec: ExecutionContext) extends MonitorService {
  val db = Database.forConfig("oso-monitor")

  //  Test.exec(configService)
  //    configService.getMonitorPersistenceContent(1l,1484898897261l).invoke().foreach(println)
  //  new MonitorSlave(configService: ConfigService, queryService: QueryService).start

  override def getMonitorResult(stage: String, mId: Int, version: Long): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    db.run(sql"select result from monitor_persistence where stage = $stage and monitor_detail_id = $mId and version = $version".as[String].head)
  }
}