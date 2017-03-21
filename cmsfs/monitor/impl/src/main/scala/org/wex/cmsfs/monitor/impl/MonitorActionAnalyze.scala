package org.wex.cmsfs.monitor.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import org.wex.cmsfs.common.CmsfsAkkaStream
import org.wex.cmsfs.config.api.{ConfigService, CoreFormatAnalyze}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem, FormatAnalyzeService}

import scala.concurrent.Future

class MonitorActionAnalyze(mt: MonitorTopic,
                           fas: FormatAnalyzeService,
                           cs: ConfigService,
                           system: ActorSystem)(implicit mi: Materializer) {

  private implicit val logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  private val supervisionStrategyFun = CmsfsAkkaStream.supervisionStrategy(logger)(_)

  private def loggerFlowFun[T](elem: T, message: String) = CmsfsAkkaStream.loggerFlow(logger)(elem, message)

  logger.info(s"${this.getClass} start.")

  /**
    * analyze format
    */
  mt.collectResultTopic.subscriber
    .map { elem => loggerFlowFun(elem, "analyze action " + elem.id) }
    .filter(_.rs.isDefined)
    .mapAsync(10) { i =>
      val coreFormatAnalyzesFuture: Future[Seq[CoreFormatAnalyze]] = cs.getCoreFormatAnalyzesByCollectId(i.id).invoke();
      for {
        coreFormatAnalyzes <- coreFormatAnalyzesFuture
        seqDone <- Future.sequence {
          coreFormatAnalyzes.map { p =>
            val formatAnalyzeItem =
              FormatAnalyzeItem(i.connectorName, p._index, p._metric, i.utcDate, i.rs.get, p.path, p.args)
            fas.pushFormatAnalyze.invoke(formatAnalyzeItem)
          }
        }
      } yield seqDone
    }.withAttributes(supervisionStrategyFun((x) => x + "xx"))
    .runWith(Sink.ignore)

  /**
    * alarm format
    */
  mt.collectResultTopic.subscriber
    .map { elem => loggerFlowFun(elem, "alarm action " + elem.id) }
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