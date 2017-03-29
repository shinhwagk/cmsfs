package org.wex.cmsfs.common.collect

trait CollectCore {
  def collectTimeMonitor: (String) => String = {
    val timestamp = System.currentTimeMillis()
    (collectName: String) => {
      val timestampAfter = System.currentTimeMillis()
      s"collect time monitor: ${collectName}, time: ${timestampAfter - timestamp}"
    }
  }
}
