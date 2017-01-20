package org.shinhwagk.monitor.monitor

import java.nio.file.Paths
import java.util.Date

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, FileIO, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source, Zip, ZipWith}
import akka.stream.{ActorMaterializer, ClosedShape, FanInShape2, IOResult}
import akka.util.ByteString
import org.quartz.CronExpression
import org.shinhwagk.config.api._
import org.shinhwagk.query.api.{QueryService, QueryOracleMessage => QOM}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by zhangxu on 2017/1/16.
  */

object MonitorSlave {
  import sys.process._
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  def aaa(configService: ConfigService, queryService: QueryService) = {
    val version = System.currentTimeMillis()
    RunnableGraph.fromGraph(GraphDSL.create() { implicit b: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._



      val in: Source[MonitorDetail, NotUsed] = Source.fromFuture(configService.getMonitorDetails.invoke()).mapConcat(_.toList)

      val fc: Flow[MonitorDetail, MonitorDetail, NotUsed] = Flow[MonitorDetail].filter(m => new CronExpression(m.cron).isSatisfiedBy(new Date()))

      val zip = b.add(ZipWith[MonitorModeJDBC, MachineConnectorModeJDBC, QOM]((l, r) => QOM(r.jdbcUrl,  r.username, r.password, l.code, List("5"))))

      val bc = b.add(Broadcast[MonitorDetail](5))

      def fm(mode: String) = Flow[MonitorDetail].filter(_.mode == mode)

      val bc2 = b.add(Broadcast[MonitorDetail](2))

      val bc3 = b.add(Broadcast[String](3))

      def mm: Flow[MonitorDetail, MonitorModeJDBC, NotUsed] =
        Flow[MonitorDetail].mapAsync(1)(md => configService.getMonitorMode("JDBC", md.monitorModeId).invoke().map(Json.parse(_).as[MonitorModeJDBC]))

      def mcm: Flow[MonitorDetail, MachineConnectorModeJDBC, NotUsed] =
        Flow[MonitorDetail].mapAsync(1)(md => configService.getMachineConnectorModeJdbc(md.machineConnectorId).invoke())

      val oq: Flow[QOM, String, NotUsed] = Flow[QOM].mapAsync(1)(queryService.queryForOracle("ARRAY").invoke(_))

      val zip2: FanInShape2[MonitorDetail, String, (MonitorDetail, String)] = b.add(Zip[MonitorDetail, String]())

      val f5: Flow[MonitorDetail, String, NotUsed] = Flow[MonitorDetail].mapAsync(1)(md=>configService.getMonitorPersistenceContent(md.id.get,version).invoke())

      val zip3: FanInShape2[MonitorDetail, NotUsed, (MonitorDetail, NotUsed)] = b.add(Zip[MonitorDetail, NotUsed]())

      val f6: Flow[MonitorPersistenceQuery, NotUsed, NotUsed] = Flow[MonitorPersistenceQuery].mapAsync(1)(p=>configService.addMonitorPersistence.invoke(p))

      val f7 = Flow[(MonitorDetail,String)].map(p=>MonitorPersistenceQuery(None,p._2,version,p._1.id.get))

      def lineSink(filename: String): Sink[String, NotUsed] =
        Flow[String]
          .map(s => ByteString(s + "\n"))
          .to(FileIO.toPath(Paths.get(filename)))

      def lineSink2(filename: String): Sink[String, Future[IOResult]] =
        Flow[String]
          .map(s => ByteString(s + "\n"))
          .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)


                                                                     bc3 ~> Flow[String].mapAsync(1)(p=> Future("powershell -help".!!)) ~> Sink.ignore

                                                    zip.out ~> oq ~> bc3 ~> Sink.ignore

                                      bc2 ~> mcm ~> zip.in1

      in ~> fc ~> bc ~> fm("JDBC") ~> bc2 ~> mm  ~> zip.in0

                  bc ~> fm("SSH")  ~> Sink.ignore

                  bc ~> Sink.foreach(println)

                  bc ~> Flow[MonitorDetail].filter(_.persistence)        ~> zip2.in0

                                                                     bc3 ~> zip2.in1

                                                                            zip2.out ~> f7 ~> f6 ~> zip3.in1

                  bc ~> Flow[MonitorDetail].filter(_.alarm) ~> zip3.in0
//      configService.getMonitorPersistenceContent(p._1.id.get,version).invoke()
                                                               zip3.out ~>
//                                                                 Flow[(MonitorDetail, NotUsed)].mapAsync(2)(p=>Future{"aaaaaaaaaa"}) ~> Sink.foreach(println)
                                                                 Flow[(MonitorDetail, NotUsed)]
                                                                 .mapAsync(1)(p=>{configService.getMonitorPersistenceContent(1,version).invoke() }) ~>
                                                                      Flow[String] ~> lineSink("aaa")
//                                                                Flow[String].map(s => ByteString(s + "\n")).mapAsync(1)(p=>FileIO.toPath(Paths.get("./aaaa"))) ~> Sink.foreach[IOResult](p=>println(p.wasSuccessful))

//      Flow[String]
//


      ClosedShape
    }).run()
  }
}