package org.wex.cmsfs.collect.ssh.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait CollectSSHService extends Service {

  def pushCollectItem: ServiceCall[CollectItemSSH, Done]

  override final def descriptor = {
    import Service._
    named("ssh.collect").withCalls(
      restCall(Method.POST, "/v1/collect", pushCollectItem)
    )
  }
}