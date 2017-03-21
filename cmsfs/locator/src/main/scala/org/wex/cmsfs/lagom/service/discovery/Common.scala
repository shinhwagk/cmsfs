package org.wex.cmsfs.lagom.service.discovery

import com.lightbend.lagom.scaladsl.server.LagomApplicationContext
import play.api.LoggerConfigurator

object Common {
  def loaderEnvironment(context: LagomApplicationContext): Unit = {
    val environment = context.playContext.environment
    LoggerConfigurator(environment.classLoader).foreach(_.configure(environment))
  }
}
