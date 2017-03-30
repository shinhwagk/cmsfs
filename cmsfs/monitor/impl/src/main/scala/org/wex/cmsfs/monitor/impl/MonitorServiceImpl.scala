package org.wex.cmsfs.monitor.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import ogr.wex.cmsfs.monitor.api.{MonitorService, MonitorStatusItem}

import scala.collection.concurrent.{Map, TrieMap}
import scala.concurrent.{ExecutionContext, Future}

class MonitorServiceImpl()(implicit ec: ExecutionContext) extends MonitorService {
  val monitoStatuses: Map[String, List[String]] = TrieMap.empty


  override def pushMonitorStatus: ServiceCall[MonitorStatusItem, Done] = ServiceCall { msi =>
    monitoStatuses.replace(msi.key, msi.status)
    Future.successful(Done)
  }

  override def getMonitorStatus(key: String): ServiceCall[NotUsed, Seq[String]] = ServiceCall { _ =>
    Future.successful(monitoStatuses.getOrElse(key, Seq.empty))
  }
}