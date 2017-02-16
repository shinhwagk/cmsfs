package org.wex.cmsfs.format.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.config.api.ConfigService
import org.wex.cmsfs.format.api.FormatService
import play.api.libs.ws.ahc.AhcWSComponents

class FormatApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new FormatApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new FormatApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
}

abstract class FormatApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with PubSubComponents {

  val configService = serviceClient.implement[ConfigService]

  override lazy val lagomServer = LagomServer.forServices(
    bindService[FormatService].to(wire[FormatServiceImpl])
  )

}