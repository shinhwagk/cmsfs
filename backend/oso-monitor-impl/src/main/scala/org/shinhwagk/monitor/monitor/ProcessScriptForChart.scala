package org.shinhwagk.monitor.monitor

import com.lightbend.lagom.scaladsl.api.Service

/**
  * Created by zhangxu on 2017/1/24.
  */
case class ProcessScriptForChart(id: Int, sr: Service) extends ProcessScript {
  override def getScript: String = ???
}
