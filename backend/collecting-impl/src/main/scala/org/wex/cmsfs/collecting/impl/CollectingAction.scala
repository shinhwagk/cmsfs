package org.wex.cmsfs.collecting.impl

import java.util.Date

import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.shinhwagk.config.api.ConfigService

import scala.concurrent.Future

class CollectingAction(persistentEntityRegistry: PersistentEntityRegistry, configService: ConfigService) {

//  def loopCollecting = Future {
//    while (true) {
//      val cDate = new Date() // currten Date
//
//      configService.getCollectDetails.invoke().foreach(_.foreach { cd =>
//        val cron = cd.cron
//        val cId = cd.ConnectorId
//        val mId = cd.monitorId
//        val mode = cd.mode
//        mode match {
//          case "JDBC" =>
//            configService.getConnectorJDBCById(cId).invoke()
//            configService.getMonitorJDBCbyId(mId).invoke()
//          case "SSH" =>
//            configService.getConnectorSSHById(cId).invoke()
//            configService.getMonitorSSHById(mId).invoke()
//        }
//
//      })
//    }
//
//    Thread.sleep(1000)
//  }

  //  def

}
