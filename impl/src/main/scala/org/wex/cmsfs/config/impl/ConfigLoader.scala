package org.wex.cmsfs.config.impl

import com.lightbend.lagom.scaladsl.client.ConfigurationServiceLocatorComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.config.api.ConfigService
import play.api.libs.ws.ahc.AhcWSComponents

class ConfigLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ConfigApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new ConfigApplication(context) with ConfigurationServiceLocatorComponents

  override def describeServices = List(
    readDescriptor[ConfigService]
  )
}

abstract class ConfigApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  override lazy val lagomServer = LagomServer.forServices(
    bindService[ConfigService].to(wire[ConfigServiceImpl])
  )
}
