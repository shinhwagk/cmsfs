package org.wex.cmsfs.format.analyze.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeService
import org.wex.cmsfs.format.analyze.impl.api.FormatAnalyzeService
import play.api.LoggerConfigurator
import play.api.libs.ws.ahc.AhcWSComponents

class ServiceApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ServiceApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication = {
    loaderEnvironment(context)
    new ServiceApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
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
    bindService[FormatAnalyzeService].to(wire[FormatAnalyzeServiceImpl])
  )

  val topic = wire[FormatAnalyzeTopic]
  val action = wire[FormatAnalyzeAction]
}
