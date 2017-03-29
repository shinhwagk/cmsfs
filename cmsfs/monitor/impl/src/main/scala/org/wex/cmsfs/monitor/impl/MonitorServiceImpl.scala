package org.wex.cmsfs.monitor.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import ogr.wex.cmsfs.monitor.api.MonitorService

import scala.concurrent.{ExecutionContext, Future}

class MonitorServiceImpl()(implicit ec: ExecutionContext) extends MonitorService {
  override def pushCollectItem: ServiceCall[NotUsed, Done] = ServiceCall { _ =>
    Future.successful(Done)
  }
}