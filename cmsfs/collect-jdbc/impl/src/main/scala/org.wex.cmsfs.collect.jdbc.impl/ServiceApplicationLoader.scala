package org.wex.cmsfs.collect.jdbc.impl

import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.collect.jdbc.api.CollectJDBCService
import org.wex.cmsfs.lagom.service.discovery.name.ServiceNameServiceLocatorComponents
import org.wex.cmsfs.monitor.api.MonitorService
import play.api.LoggerConfigurator
import play.api.libs.ws.ahc.AhcWSComponents

class ServiceApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    loaderEnvironment(context)
    new ServiceApplication(context) with LagomDevModeComponents
  }

  override def load(context: LagomApplicationContext): LagomApplication = {
    loaderEnvironment(context)
    new ServiceApplication(context) with ServiceNameServiceLocatorComponents
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
    bindService[CollectJDBCService].to(wire[CollectJDBCServiceImpl])
  )

  val monitorService = serviceClient.implement[MonitorService]

  val collectTopic = wire[CollectTopic]
  val collecting = wire[Collecting]
}