package org.wex.cmsfs.collect.ssh.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.slf4j.{Logger, LoggerFactory}
import org.wex.cmsfs.format.alarm.api.FormatAlarmService
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeService
import play.api.Configuration

import scala.concurrent.ExecutionContextExecutor

class Collecting(ct: CollectTopic,
                 analyzeService: FormatAnalyzeService,
                 alarmService: FormatAlarmService,
                 override val config: Configuration,
                 system: ActorSystem)(implicit mat: Materializer)
  extends CollectSsh(analyzeService, alarmService) {

  override val logger: Logger = LoggerFactory.getLogger(this.getClass)

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  ct.CollectTopic.subscriber
    .via(flow).withAttributes(supervisionStrategy((x => s"${x} y")))
    .runWith(Sink.foreach(id => logger.info(s"id:${id}, collect success.")))
}
