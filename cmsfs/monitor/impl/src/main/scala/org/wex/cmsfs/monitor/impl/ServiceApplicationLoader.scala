package org.wex.cmsfs.monitor.impl

import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.collect.jdbc.api.CollectJDBCService
import org.wex.cmsfs.collect.ssh.api.CollectSSHService
import org.wex.cmsfs.config.api.ConfigService
import org.wex.cmsfs.lagom.service.discovery.consul.ConsulServiceLocatorComponents
import org.wex.cmsfs.monitor.api.MonitorService
import play.api.LoggerConfigurator
import play.api.libs.ws.ahc.AhcWSComponents

class ServiceApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ServiceApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication = {
    loaderEnvironment(context)
    new ServiceApplication(context) with ConsulServiceLocatorComponents
  }

  def loaderEnvironment(context: LagomApplicationContext): Unit = {
    val environment = context.playContext.environment
    LoggerConfigurator(environment.classLoader).foreach(_.configure(environment))
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
  val collectSSHService = serviceClient.implement[CollectSSHService]
  val collectJDBCService = serviceClient.implement[CollectJDBCService]
  //  val queryService = serviceClient.implement[QueryService]
  //  val formatService = serviceClient.implement[FormatService]

  val monitorTopic = wire[MonitorTopic]
  val monitorAction = wire[MonitorAction]
  //  val monitorActionAnalyze = wire[MonitorActionAnalyze]
}