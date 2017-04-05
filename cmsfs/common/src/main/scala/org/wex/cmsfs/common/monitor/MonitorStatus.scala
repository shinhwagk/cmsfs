package org.wex.cmsfs.common.monitor

import java.time.{ZoneOffset, ZonedDateTime}

import com.redis.RedisClient
import play.api.libs.json._

trait MonitorStatus {
  val r = new RedisClient("redis.cmsfs.org", 6379)

  def putStatus(id: Int, stage: String)(state: Boolean, result: String) = {
    val stageStatus = CoreMonitorStageStatus(state, result)
    val key = s"${id}_${stage}"

    r.set(key, Json.toJson(stageStatus).toString())
  }
}

case class CoreMonitorStatus(id: Int, category: String, name: String, metric: String,
                             collect: CoreMonitorCollectStatus,
                             analyze: Option[CoreMonitorAnalyzeStatus],
                             Alarm: Option[CoreMonitorAlarmStatus])

object CoreMonitorStatus {
  implicit val format: Format[CoreMonitorStatus] = Json.format
}

case class CoreMonitorStageStatus(state: Boolean, result: String, timestamp: String = ZonedDateTime.now(ZoneOffset.ofHours(8)).toString)

object CoreMonitorStageStatus {
  implicit val format: Format[CoreMonitorStageStatus] = Json.format
}

case class CoreMonitorCollectStatus(state: Boolean, timestamp: String, result: String)

object CoreMonitorCollectStatus {
  implicit val format: Format[CoreMonitorCollectStatus] = Json.format
}

case class CoreMonitorAnalyzeStatus(state: Boolean, timestamp: String, result: String)

object CoreMonitorAnalyzeStatus {
  implicit val format: Format[CoreMonitorAnalyzeStatus] = Json.format
}

case class CoreMonitorAlarmStatus(state: Boolean, timestamp: String, result: String)

object CoreMonitorAlarmStatus {
  implicit val format: Format[CoreMonitorAlarmStatus] = Json.format
}