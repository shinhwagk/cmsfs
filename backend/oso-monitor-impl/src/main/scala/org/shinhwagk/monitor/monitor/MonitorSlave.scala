package org.shinhwagk.monitor.monitor

import java.util.Date

import akka.NotUsed
import akka.stream.ClosedShape
import akka.stream.javadsl.Unzip
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source, UnzipWith}
import org.quartz.CronExpression
import org.shinhwagk.config.api.{ConfigService, Monitor, MonitorModeJDBC, MonitorModeSSH}
import play.api.libs.concurrent.Promise
import play.api.libs.json.Json

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by zhangxu on 2017/1/16.
  */
//object MonitorSlave {
////  def apply(configService: ConfigService):MonitorSlave = MonitorSlave(configService)
//
//
//}


object MonitorSlave {
  def aaa(configService: ConfigService) = {
    val c: Future[List[Monitor]] = configService.getMonitorList.invoke()

    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val in: Source[Monitor, NotUsed] = Source.fromFuture(c).mapConcat(_.toList)

      val filterCron: Flow[Monitor, Monitor, NotUsed] = Flow[Monitor].filter(m => new CronExpression(m.cron).isSatisfiedBy(new Date()))

      def filterMode(mode: String) = Flow[Monitor].filter(_.mode == mode)

      def jdbc: Flow[(String, Int), MonitorModeJDBC, NotUsed] = Flow[(String, Int)].mapAsync(1)(mon => configService.getMonitorMode(mon._1, mon._2).invoke().map(Json.parse(_).as[MonitorModeJDBC]))

      //      def ssh = Flow[Monitor].mapAsync(1)(mon => configService.getMonitorMode(mon.mode, mon.modeId).invoke().map(Json.parse(_).as[MonitorModeSSH]))

      val bcast = builder.add(Broadcast[Monitor](2))

      val unzip = builder.add(UnzipWith[Monitor, (String, Int), Int]((mon: Monitor) => ((mon.mode, mon.modeId), mon.id.get)))


      in ~> filterCron ~> bcast ~> filterMode("JDBC") ~> unzip.in

                          bcast ~> filterMode("SSH")

      unzip.out0 ~> jdbc

      //      val in = Source(1 to 10)
      //      val out = Sink.ignore
      //


      //      val f1, f2, f3, f4 = Flow[Int].map(_ + 10)
      //
      //      in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
      //      bcast ~> f4 ~> merge
      ClosedShape
    })
  }


}
