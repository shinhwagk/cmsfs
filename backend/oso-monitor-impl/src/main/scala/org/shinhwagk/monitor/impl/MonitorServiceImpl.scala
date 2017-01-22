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

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Implementation of the LagomhelloService.
  */
class MonitorServiceImpl(configService: ConfigService, queryService: QueryService)(implicit ec: ExecutionContext) extends MonitorService {
  //  Test.exec(configService)
  //    configService.getMonitorPersistenceContent(1l,1484898897261l).invoke().foreach(println)
  new MonitorSlave(configService: ConfigService, queryService: QueryService).start

  override def test: ServiceCall[NotUsed, NotUsed] = ServiceCall { _ =>
    Future.successful(akka.NotUsed)
  }
}
