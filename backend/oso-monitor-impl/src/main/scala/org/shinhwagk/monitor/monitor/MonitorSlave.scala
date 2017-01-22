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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by zhangxu on 2017/1/16.
  */

class MonitorSlave(cs: ConfigService, queryService: QueryService) {

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

  def sourcex(version:Long): RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() { implicit b: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    val in: Source[MonitorDetail, NotUsed] = Source.fromFuture(cs.getMonitorDetails.invoke()).mapConcat(_.toList)

    val fc: Flow[MonitorDetail, MonitorDetail, NotUsed] = Flow[MonitorDetail].filter(m => new CronExpression(m.cron).isSatisfiedBy(new Date()))

    def fm(mode: MonitorModeEnum) = Flow[MonitorDetail].filter(_.mode == mode)

    val ff1: Flow[MonitorDetail, ProcessOriginal, NotUsed] =
      Flow[MonitorDetail].map(ProcessOriginalForJDBC(_))
    val ff2  =
      b.add(Flow[ProcessOriginal].mapAsync(1)(_.getMonitor(cs)))
    val ff3: Flow[ProcessOriginal, ProcessOriginal, NotUsed] =
      Flow[ProcessOriginal].mapAsync(1)(_.getConnector(cs))
    val ff4: Flow[ProcessOriginal, ProcessOriginal, NotUsed] =
      Flow[ProcessOriginal].mapAsync(1)(_.query(queryService))

    val bd = b.add(Broadcast[MonitorDetail](2))

    val merge = b.add(Merge[ProcessOriginal](1))

    //    val bd2 = b.add(Zip[MonitorDetail, Boolean]())

    //    val ff5 = b.add(Flow[(MonitorDetail, Boolean)].filter(_._2).map(_._1))

    val ap: FlowShape[ProcessOriginal, Boolean] = b.add(Flow[ProcessOriginal].mapAsync(1)(p => cs.addMonitorPersistence.invoke(p.genPersistence(version)).map(_ => true)))

    in ~> fc ~> bd ~> fm(MonitorModeEnum.JDBC) ~> ff1 ~> merge
                bd ~> fm(MonitorModeEnum.SSH)                                    ~> Sink.ignore


                                           merge ~> ff2 ~> ff3 ~> ff4 ~> ap ~> Sink.ignore
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