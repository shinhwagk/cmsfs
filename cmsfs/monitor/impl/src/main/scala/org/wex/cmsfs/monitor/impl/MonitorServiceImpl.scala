package org.wex.cmsfs.monitor.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.redis.RedisClient
import ogr.wex.cmsfs.monitor.api.MonitorService
import org.wex.cmsfs.config.api.{ConfigService, CoreMonitorStageStatus, CoreMonitorStatus}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

class MonitorServiceImpl(configService: ConfigService)(implicit ec: ExecutionContext) extends MonitorService {

  val r = new RedisClient("redis.cmsfs.org", 6379)

  override def getMonitorStatus: ServiceCall[NotUsed, Seq[CoreMonitorStatus]] = ServiceCall { _ =>
    configService.getCoreMonitorStatuses.invoke().map(_.map { cms =>
      val collect: Option[CoreMonitorStageStatus] = r.get(s"${cms.id}_COLLECT").map(Json.parse(_).as[CoreMonitorStageStatus])
      val analyze: Option[CoreMonitorStageStatus] = r.get(s"${cms.id}_ANALYZE").map(Json.parse(_).as[CoreMonitorStageStatus])
      val alarm: Option[CoreMonitorStageStatus] = r.get(s"${cms.id}_ALARM").map(Json.parse(_).as[CoreMonitorStageStatus])
      cms.copy(collect = collect, analyze = analyze, alarm = alarm)
    }.sortBy(_.id))
  }
}