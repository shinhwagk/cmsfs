package org.shinhwagk.monitor.monitor

import java.nio.file.Paths
import java.util.Date

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Balance, Broadcast, FileIO, Flow, GraphDSL, Keep, Merge, RunnableGraph, Sink, Source, Zip, ZipWith}
import akka.stream._
import akka.util.ByteString
import org.quartz.CronExpression
import org.shinhwagk.config.api._
import org.shinhwagk.monitor.monitor.MonitorModeEnum.MonitorModeEnum
import org.shinhwagk.query.api.{QueryService, QueryOracleMessage => QOM}
import play.api.libs.json.Json

import scala.collection.immutable.Nil
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by zhangxu on 2017/1/16.
  */

class MonitorSlave(cs: ConfigService, qs: QueryService) {

  import sys.process._

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val processReport =
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val balance = b.add(Balance[MonitorDetail](2))
      val merge = b.add(Merge[MonitorDetail](2))
      balance.out(0) ~> merge
      balance.out(1) ~> merge
      FlowShape(balance.in, merge.out)
    })

  val processAlarm =
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val balance = b.add(Balance[MonitorDetail](2))
      val merge = b.add(Merge[MonitorDetail](2))
      balance.out(0) ~> merge
      balance.out(1) ~> merge
      FlowShape(balance.in, merge.out)
    })

  val processChart =
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val balance = b.add(Balance[MonitorDetail](2))
      val merge = b.add(Merge[MonitorDetail](2))
      balance.out(0) ~> merge
      balance.out(1) ~> merge
      FlowShape(balance.in, merge.out)
    })

  //  val version = System.currentTimeMillis()

  val version = System.currentTimeMillis()

  def sourcex(version: Long): RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() { implicit b: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    val in: Source[MonitorDetail, NotUsed] = Source.fromFuture(cs.getMonitorDetails.invoke()).mapConcat(_.toList)

    val fc: Flow[MonitorDetail, MonitorDetail, NotUsed] = Flow[MonitorDetail].filter(m => new CronExpression(m.cron).isSatisfiedBy(new Date()))

    def fm(mode: MonitorModeEnum) =
      mode match {
        case MonitorModeEnum.JDBC =>
          Flow[MonitorDetail].filter(_.mode == MonitorModeEnum.JDBC).map(ProcessOriginalForJDBC(_, version, cs, qs))
        case MonitorModeEnum.SSH =>
          Flow[MonitorDetail].filter(_.mode == MonitorModeEnum.JDBC).map(ProcessOriginalForJDBC(_, version, cs, qs))
      }

    val ff2: FlowShape[ProcessOriginal, ProcessOriginal] =
      b.add(Flow[ProcessOriginal].mapAsync(1)(_.getMonitor))
    val ff3: Flow[ProcessOriginal, ProcessOriginal, NotUsed] =
      Flow[ProcessOriginal].mapAsync(1)(_.getConnector)
    val ff4: Flow[ProcessOriginal, ProcessOriginal, NotUsed] =
      Flow[ProcessOriginal].mapAsync(1)(_.query)

    def filterProcessScript(sc:String): Flow[ProcessOriginal, ProcessScript, NotUsed] =
      sc match {
        case "ALARM" => Flow[ProcessOriginal].filter(_.ProcessAlarm.isDefined).map(_.ProcessAlarm.get)
        case "REPORT" => Flow[ProcessOriginal].filter(_.ProcessReport.isDefined).map(_.ProcessReport.get)
        case "CHART" => Flow[ProcessOriginal].filter(_.ProcessChart.isDefined).map(_.ProcessChart.get)
      }

    val bd = b.add(Broadcast[MonitorDetail](2))

    val merge = b.add(Merge[ProcessOriginal](1))

    val bd2 = b.add(Broadcast[ProcessOriginal](3))

    val merge2 = b.add(Merge[ProcessScript](3))

    val ap: FlowShape[ProcessOriginal, ProcessOriginal] =
      b.add(Flow[ProcessOriginal].mapAsync(1)(_.genPersistence))

    val ffff: Flow[ProcessOriginal,List[ProcessOriginal], NotUsed] = Flow[ProcessOriginal].fold(List[ProcessOriginal]())((a, f) => a:+f  )

    in ~> fc ~> bd ~> fm(MonitorModeEnum.JDBC) ~> merge
                bd ~> fm(MonitorModeEnum.SSH)                                              ~> Sink.ignore

                                                  merge ~> ff2 ~> ff3 ~> ff4 ~> ap ~> bd2

                                           merge2 <~ filterProcessScript("ALARM")  <~ bd2
                                           merge2 <~ filterProcessScript("REPORT") <~ bd2
                                           merge2 <~ filterProcessScript("CHART")  <~ bd2

                                                  merge2 ~> Flow[ProcessScript].map(_.getScript)

    ClosedShape
  })

  def start = {
    while (true) {
      Future(sourcex(System.currentTimeMillis()).run())
      //      c.foreach(p => println("success"))
      Thread.sleep(1000)
    }
  }
}