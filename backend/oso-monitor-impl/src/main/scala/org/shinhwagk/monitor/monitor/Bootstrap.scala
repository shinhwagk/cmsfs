package org.shinhwagk.monitor.monitor

import org.shinhwagk.config.api.ConfigService

/**
  * Created by zhangxu on 2017/1/16.
  */
object Bootstrap extends App {
  def main(configService: ConfigService): Unit = {
    val mds = configService.getMonitorDetails.invoke()


  }
}