package org.wex.cmsfs.common.collect

trait CollectCore {
  def collectTimeMonitor: (String) => String = {
    val timestamp = System.currentTimeMillis()
    def collectTimeMonitorCalculate(collectName: String) = {
      val timestapAfter = System.currentTimeMillis()
      s"collect time monitor: ${collectName}, time: ${timestapAfter - timestamp}"
    }
    collectTimeMonitorCalculate _
  }
}
