package org.wex.cmsfs.collect.ssh.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import org.wex.cmsfs.common.`object`.CoreMonitorDetailForSsh

object CollectSSHService {
  val SERVICE_NAME = "collect-ssh"
}

trait CollectSSHService extends Service {

  def pushCollectItem: ServiceCall[CollectItemSsh, Done]

  def pushCollectItem2: ServiceCall[CoreMonitorDetailForSsh, Done]

  override final def descriptor = {
    import Service._
    import CollectSSHService._
    named(SERVICE_NAME).withCalls(
      restCall(Method.POST, "/v1/collect", pushCollectItem)
    )
  }
}