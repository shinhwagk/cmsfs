package org.wex.cmsfs.monitor.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.shinhwagk.query.api.QueryService
import org.wex.cmsfs.config.api.ConfigService
import org.wex.cmsfs.format.api.FormatService
import org.wex.cmsfs.monitor.api.MonitorService
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.client.ConfigurationServiceLocatorComponents
import play.api.LoggerConfigurator

class ServiceApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ServiceApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication = {
    val environment = context.playContext.environment
    LoggerConfigurator(environment.classLoader).foreach(_.configure(environment))
    new ServiceApplication(context) with ConfigurationServiceLocatorComponents
  }
}

abstract class ServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with PubSubComponents {

  override lazy val lagomServer = LagomServer.forServices(
    bindService[MonitorService].to(wire[MonitorServiceImpl])
  )

  val configService = serviceClient.implement[ConfigService]
  val queryService = serviceClient.implement[QueryService]
  val formatService = serviceClient.implement[FormatService]

  val monitorTopic = wire[MonitorTopic]
  val monitorAction = wire[MonitorAction]
  val monitorActionCollect = wire[MonitorActionCollect]
  val monitorActionAnalyze = wire[MonitorActionAnalyze]
}