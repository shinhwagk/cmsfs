package org.wex.cmsfs.monitor.impl

import com.lightbend.lagom.scaladsl.client.LagomServiceClientComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import ogr.wex.cmsfs.monitor.api.MonitorService
import org.wex.cmsfs.collect.jdbc.api.CollectJDBCService
import org.wex.cmsfs.collect.ssh.api.CollectSSHService
import org.wex.cmsfs.config.api.ConfigService
import org.wex.cmsfs.lagom.service.discovery.Common
import org.wex.cmsfs.lagom.service.discovery.consul.ConsulServiceLocatorComponents
import play.api.libs.ws.ahc.AhcWSComponents

class ServiceApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication = {
    Common.loaderEnvironment(context)
    new ServiceApplication(context) with ConsulServiceLocatorComponents
  }
}

abstract class ServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with LagomServiceClientComponents {

  override lazy val lagomServer = LagomServer.forServices(
    bindService[MonitorService].to(wire[MonitorServiceImpl])
  )

  val configService = serviceClient.implement[ConfigService]
  val collectSSHService = serviceClient.implement[CollectSSHService]
  val collectJDBCService = serviceClient.implement[CollectJDBCService]

  val bootstrapActionCollect = wire[MonitorActionCollect]
}