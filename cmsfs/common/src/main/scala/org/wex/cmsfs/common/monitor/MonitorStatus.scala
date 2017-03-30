package org.wex.cmsfs.common.monitor

import ogr.wex.cmsfs.monitor.api.MonitorService

trait MonitorStatus {
  val monitorService: MonitorService

  def putStatus(stage: String, error: Option[String]) = {
    monitorService.
  }
}
