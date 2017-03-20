package org.wex.cmsfs.monitor.impl

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{ActorAttributes, Materializer, Supervision}
import akka.stream.scaladsl.Sink
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.config.api.{ConfigService, CoreFormatAnalyze}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem, FormatAnalyzeService}

import scala.concurrent.Future

class MonitorActionAnalyze(mt: MonitorTopic,
                           fas: FormatAnalyzeService,
                           cs: ConfigService,
                           system: ActorSystem)(implicit mi: Materializer) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private implicit val executionContext = system.dispatcher

  logger.info(s"${this.getClass} start.")

  /**
    * analyze format
    */
  mt.collectResultTopic.subscriber
    .map { elem => logger.info("analyze action " + elem.id); elem }
    .filter(_.rs.isDefined)
    .mapAsync(10) { i =>
      val coreFormatAnalyzesFuture: Future[Seq[CoreFormatAnalyze]] = cs.getCoreFormatAnalyzesByCollectId(i.id).invoke();
      for {
        coreFormatAnalyzes <- coreFormatAnalyzesFuture
        seqDone <- Future.sequence {
          coreFormatAnalyzes.map(p => fas.pushFormatAnalyze.invoke(FormatAnalyzeItem(p, i)))
        }
      } yield seqDone
    }.withAttributes(ActorAttributes.supervisionStrategy(decider))
    .runWith(Sink.ignore)

  /**
    * alarm format
    */
  mt.collectResultTopic.subscriber
    .map { elem => logger.info("alarm action " + elem.id); elem }
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
  def decider(implicit log: Logger): Supervision.Decider = {
    case ex: Exception =>
      log.error(ex.getMessage + " XXXX")
      Supervision.Resume
  }
}