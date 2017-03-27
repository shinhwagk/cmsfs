package org.wex.cmsfs.monitor.status.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.collection.concurrent.{Map, TrieMap}
import scala.concurrent.{ExecutionContext, Future}

class MonitorStatusServiceImpl()(implicit ec: ExecutionContext) extends MonitorStatusService {

  private val coreMonitorStatusMap: Map[Int, CoreMonitorStatus] = TrieMap.empty[Int, CoreMonitorStatus]

  override def putCoreMonitorStatus(id: Int, category: String, name: String, metric: String): ServiceCall[NotUsed, Done] = ServiceCall { _ =>
    Future {
      val coreMonitorStatus = CoreMonitorStatus(id, category, name, metric, None, None, None)
      coreMonitorStatusMap.putIfAbsent(id, coreMonitorStatus)
    }.map(_ => Done)
  }

  override def putCoreMonitorStatusCollect(id: Int): ServiceCall[CoreMonitorStatusCollect, Done] = ServiceCall { cmsc =>
    Future {
      val coreMonitorStatusOpt: Option[CoreMonitorStatus] = coreMonitorStatusMap.get(id)
      coreMonitorStatusOpt.map(cms => cms.copy(collect = Some(cmsc)))
    }.map(_ => Done)
  }

  override def putCoreMonitorStatusAnalyze(id: Int): ServiceCall[CoreMonitorStatusAnalyze, Done] = ServiceCall { cmsa =>
    Future {
      val coreMonitorStatusOpt: Option[CoreMonitorStatus] = coreMonitorStatusMap.get(id)
      coreMonitorStatusOpt.map(cms => cms.copy(analyze = Some(cmsa)))
    }.map(_ => Done)
  }

  override def putCoreMonitorStatusAlarm(id: Int): ServiceCall[CoreMonitorStatusAlarm, Done] = ServiceCall { cmsa =>
    Future {
      val coreMonitorStatusOpt: Option[CoreMonitorStatus] = coreMonitorStatusMap.get(id)
      coreMonitorStatusOpt.map(cms => cms.copy(alarm = Some(cmsa)))
    }.map(_ => Done)
  }

  override def getCoreMonitorStatuses: ServiceCall[NotUsed, Seq[CoreMonitorStatus]] = ServiceCall { _ =>
    Future.successful(coreMonitorStatusMap.map(_._2).toSeq)
  }
}
