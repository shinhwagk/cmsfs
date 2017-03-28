package org.wex.cmsfs.collect.ssh.impl

import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.wex.cmsfs.collect.ssh.api.CollectSSHService
import org.wex.cmsfs.format.alarm.api.FormatAlarmService
import org.wex.cmsfs.format.analyze.api.FormatAnalyzeService
import org.wex.cmsfs.lagom.service.discovery.Common
import org.wex.cmsfs.lagom.service.discovery.consul.ConsulServiceLocatorComponents
import play.api.libs.ws.ahc.AhcWSComponents

class ServiceApplicationLoader extends LagomApplicationLoader {

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new ServiceApplication(context) with LagomDevModeComponents
  }

  override def load(context: LagomApplicationContext): LagomApplication = {
    Common.loaderEnvironment(context)
    new ServiceApplication(context) with ConsulServiceLocatorComponents
  }

}

abstract class ServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with PubSubComponents {

  override lazy val lagomServer = LagomServer.forServices(
    bindService[CollectSSHService].to(wire[CollectSSHServiceImpl])
  )

  val formatAlarmService = serviceClient.implement[FormatAlarmService]
  val formatAnalyzeService = serviceClient.implement[FormatAnalyzeService]

  val collectTopic = wire[CollectTopic]
  val collecting = wire[Collecting]
}