package org.wex.cmsfs.monitor.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.LoggerFactory
import org.wex.cmsfs.common.CmsfsAkkaStream
import org.wex.cmsfs.config.api.{ConfigService, CoreMonitorDetail}
import org.wex.cmsfs.format.analyze.api.{FormatAnalyzeItem, FormatAnalyzeService}
import scala.concurrent.Future

class MonitorActionAnalyze(mt: MonitorTopic,
                           fas: FormatAnalyzeService,
                           cs: ConfigService,
                           system: ActorSystem)(implicit mi: Materializer) extends CmsfsAkkaStream {

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
    }.withAttributes(supervisionStrategy((x) => x + "xx"))
    .runWith(Sink.ignore)
}