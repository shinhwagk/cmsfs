package org.wex.cmsfs.config.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.client.ConfigurationServiceLocatorComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.config.api.ConfigService
import org.wex.cmsfs.monitor.api.MonitorService
import play.api.libs.ws.ahc.AhcWSComponents

class ConfigLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ConfigApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new ConfigApplication(context) with ConfigurationServiceLocatorComponents
}

abstract class ConfigApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  val monitorService = serviceClient.implement[MonitorService]

  override lazy val lagomServer = LagomServer.forServices(
    bindService[ConfigService].to(wire[ConfigServiceImpl])
  )
}
