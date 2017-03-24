package org.wex.cmsfs.format.alarm.impl

import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.lagom.service.discovery.consul.ConsulServiceLocatorComponents
import play.api.LoggerConfigurator
import play.api.libs.ws.ahc.AhcWSComponents
import org.wex.cmsfs.format.alarm.api.FormatAlarmService
import org.wex.cmsfs.notification.impl.NotificationService

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
    bindService[FormatAlarmService].to(wire[FormatAlarmServiceImpl])
  )

  val notificationService = serviceClient.implement[NotificationService]

  val topic = wire[FormatAlarmTopic]
  val action = wire[FormatAlarmAction]
}