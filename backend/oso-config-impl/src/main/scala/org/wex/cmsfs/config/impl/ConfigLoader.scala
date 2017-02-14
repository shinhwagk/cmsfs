package org.wex.cmsfs.config.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.collecting.api.CollectingService
import org.wex.cmsfs.config.api.ConfigService
import play.api.libs.ws.ahc.AhcWSComponents

class ConfigLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ConfigApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ConfigApplication(context) with LagomDevModeComponents
}

abstract class ConfigApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  override lazy val lagomServer = LagomServer.forServices(
    bindService[ConfigService].to(wire[ConfigServiceImpl])
  )

  val collectingService = serviceClient.implement[CollectingService]

  wire[Testx]

}
