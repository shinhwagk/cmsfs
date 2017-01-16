package org.shinhwagk.monitor.monitor

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.shinhwagk.config.api.{ConfigService, MonitorDetail}

/**
  * Created by zhangxu on 2017/1/16.
  */
object MonitorSlave extends App {
  val configService: ConfigService

  def main(configService: ConfigService): Unit = {
    val mds = configService.getMonitorDetails.invoke()
  }
  val source = Source(List((1, "xxx")))
  val flow1: Flow[(Int, String), List[MonitorDetail], NotUsed] = Flow[(Int, String)].mapAsync(1) { a =>
    configService.getMonitorItem(a._2, a._1).invoke()
  }.to(Sink.foreach(p))
}