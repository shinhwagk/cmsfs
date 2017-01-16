package org.shinhwagk.monitor.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.shinhwagk.config.api.ConfigService
import org.shinhwagk.config.impl.MonitorServiceImpl
import org.shinhwagk.monitor.api.MonitorService
import play.api.libs.ws.ahc.AhcWSComponents

class MonitorLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new MonitorApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MonitorApplication(context) with LagomDevModeComponents
}

abstract class MonitorApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[MonitorService].to(wire[MonitorServiceImpl])
  )

  lazy val configService = serviceClient.implement[ConfigService]
  // Register the lagom-hello persistent entity
  //persistentEntityRegistry.register(wire[LagomhelloEntity])
}
