package org.wex.cmsfs.monitor.impl

import com.lightbend.lagom.scaladsl.pubsub.PubSubRef
import org.wex.cmsfs.config.api.MonitorDepository

class MonitorActionAlarm {
  def start(topic:PubSubRef[MonitorDepository]): Unit ={
  }



//  def executeMonitorForAnalyze(md: MonitorDepository): Future[String] = {
//    for {
//      metric <- cs.getMetricById(md.monitorId).invoke()
//      c <- cs.getConnectorSSHById(md.ConnectorId).invoke()
//      mh <- cs.getMachineById(c.machineId).invoke()
//      collectData <- qs.queryForOSScript
//        .invoke(QueryOSMessage(mh.ip, c.user, genUrl("COLLECT", "SSH", metric.name), Some(c.port)))
//    } yield collectData
//  }
}
