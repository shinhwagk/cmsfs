package org.shinhwagk.config.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.config.api.ConfigService
import org.shinhwagk.monitor.api.MonitorService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of the LagomhelloService.
  */
class MonitorServiceImpl(configService: ConfigService)(implicit ec: ExecutionContext) extends MonitorService {
//  Test.exec(configService)

  override def test: ServiceCall[NotUsed, NotUsed] = ServiceCall { _ =>
    Future.successful(akka.NotUsed)
  }
}
