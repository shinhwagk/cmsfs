package org.shinhwagk.monitor.api

import com.lightbend.lagom.scaladsl.api.Service
import com.lightbend.lagom.scaladsl.api.Service.named
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json._

/**
  * Created by zhangxu on 2017/1/10.
  */
trait MonitorService extends Service{

  def test: ServiceCall[NotUsed, NotUsed]

  override final def descriptor = {
    named("oso-monitor").withCalls(
      restCall(Method.POST, "/api/query/oracle/:mode", test)
//      restCall(Method.POST, "/api/query/os", queryForOSScript _)
    ).withAutoAcl(true)
  }
}
