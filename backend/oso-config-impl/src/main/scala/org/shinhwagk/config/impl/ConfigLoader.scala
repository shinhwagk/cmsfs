package org.shinhwagk.config.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.shinhwagk.config.api.ConfigService
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

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[ConfigService].to(wire[ConfigServiceImpl])
  )

//  lazy val configService = serviceClient.implement[ConfigService]
  // Register the lagom-hello persistent entity
  //persistentEntityRegistry.register(wire[LagomhelloEntity])
}
