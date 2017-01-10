package org.shinhwagk.monitor.api

import com.lightbend.lagom.scaladsl.api.Service
import com.lightbend.lagom.scaladsl.api.Service.named

/**
  * Created by zhangxu on 2017/1/10.
  */
trait MonitorService extends Service{

  override final def descriptor = {
    named("oso-monitor").withCalls(
//      restCall(Method.POST, "/api/query/oracle/:mode", queryForOracle _),
//      restCall(Method.POST, "/api/query/os", queryForOSScript _)
    ).withAutoAcl(true)
  }
}
