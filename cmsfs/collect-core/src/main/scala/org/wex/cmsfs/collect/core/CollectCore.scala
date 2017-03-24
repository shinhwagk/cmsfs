package org.wex.cmsfs.collect.core

import org.slf4j.Logger
import play.api.Configuration
import play.api.libs.json.Json

trait CollectCore {

  val config: Configuration


  private val formatUrl: String = config.getString("collect.url").get

  def genUrl(path: String): String = {
    formatUrl :: Json.parse(path).as[List[String]] mkString "/"
  }

  def collectTimeMonitor: (String) => String = {
    val timestamp = System.currentTimeMillis()
    def collectTimeMonitorCalculate(collectName: String) = {
      val timestapAfter = System.currentTimeMillis()
      s"collect time monitor: ${collectName}, time: ${timestapAfter - timestamp}"
    }
    collectTimeMonitorCalculate _
  }
}
