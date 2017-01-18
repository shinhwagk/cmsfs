package org.shinhwagk.config.impl

import akka.NotUsed
import akka.actor.ActorSystem
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.config.api.ConfigService
import org.shinhwagk.monitor.api.MonitorService
import org.shinhwagk.monitor.monitor.MonitorSlave

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of the LagomhelloService.
  */
class MonitorServiceImpl(configService: ConfigService, system: ActorSystem)(implicit ec: ExecutionContext) extends MonitorService {
  //  Test.exec(configService)
  //  MonitorSlave.aaa(configService)

  override def test: ServiceCall[NotUsed, NotUsed] = ServiceCall { _ =>
    Future.successful(akka.NotUsed)
  }
}
