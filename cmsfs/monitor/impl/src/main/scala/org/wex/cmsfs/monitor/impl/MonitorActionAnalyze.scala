package org.wex.cmsfs.monitor.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import org.wex.cmsfs.common.CmsfsAkka
import org.wex.cmsfs.config.api.{ConfigService, CoreFormatAnalyze, CoreMonitorDetail}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem, FormatAnalyzeService}

import scala.concurrent.Future

class MonitorActionAnalyze(mt: MonitorTopic,
                           fas: FormatAnalyzeService,
                           cs: ConfigService,
                           system: ActorSystem)(implicit mi: Materializer) extends CmsfsAkka {

  implicit val logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  logger.info(s"${this.getClass} start.")

  /**
    * analyze format
    */
  mt.collectResultTopic.subscriber
    .map { elem => loggerFlow(elem, "analyze action " + elem.monitorDetailId) }
    .filter(_.rs.isDefined)
    .mapAsync(10) { i =>
      val monitorDetailId = i.monitorDetailId
      val coreMonitorDetailFuture: Future[CoreMonitorDetail] = cs.getCoreMonitorDetailById(monitorDetailId).invoke()
      for {
        coreMonitorDetail <- coreMonitorDetailFuture if coreMonitorDetail.formatAnalyzeId.isDefined
        coreFormatAnalyze <- cs.getCoreFormatAnalyzesById(coreMonitorDetail.formatAnalyzeId.get).invoke()
        done <- {
          val p = coreFormatAnalyze
          val formatAnalyzeItem =
            FormatAnalyzeItem(i.connectorName, p._index, p._metric, i.utcDate, i.rs.get, p.path, coreMonitorDetail.formatAnalyzeArgs.getOrElse("[]"))
          fas.pushFormatAnalyze.invoke(formatAnalyzeItem)
        }
      } yield done

      //      val coreFormatAnalyzesFuture: Future[CoreFormatAnalyze] = cs.getCoreFormatAnalyzesById(i.monitorDetailId).invoke();
      //      for {
      //        coreFormatAnalyzes <- coreFormatAnalyzesFuture
      //        seqDone <- Future.sequence {
      //          coreFormatAnalyzes.map { p =>
      //            val formatAnalyzeItem =
      //              FormatAnalyzeItem(i.connectorName, p._index, p._metric, i.utcDate, i.rs.get, p.path, p.args.getOrElse("[]"))
      //            fas.pushFormatAnalyze.invoke(formatAnalyzeItem)
      //          }
      //        }
      //      } yield seqDone
    }.withAttributes(supervisionStrategy((x) => x + "xx"))
    .runWith(Sink.ignore)

  /**
    * alarm format
    */
  mt.collectResultTopic.subscriber
    .map { elem => loggerFlow(elem, "alarm action " + elem.monitorDetailId) }
    .filter(_.rs.isDefined)
    //    .mapAsync(10)(x => fas.pushFormatAnalyze.invoke(FormatAnalyzeItem(x.id, x.metricName, x.rs.get, "[]", x.utcDate, x.name)))
    .runWith(Sink.ignore)

  //    .subscriber
  //    .map { p => log.info("fasong  anglay "); p }
  //    .mapAsync(10)(md => fs.pushFormatAnalyze(md.metricName, md.collectData).invoke())
  //    .runWith(Sink.ignore)

  //  def executeMonitorForAnalyze(md: MonitorActionDepository): Future[String] = {
  //    for {
  //
  //      fs.pushFormatAnalyze()
  //      metric <- cs.getMetricById(md.monitorId).invoke()
  //      c <- cs.getConnectorSSHById(md.ConnectorId).invoke()
  //      mh <- cs.getMachineById(c.machineId).invoke()
  //      collectData <- qs.queryForOSScript
  //        .invoke(QueryOSMessage(mh.ip, c.user, genUrl("COLLECT", "SSH", metric.name), Some(c.port)))
  //    } yield collectData
  //  }

}