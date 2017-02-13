package org.wex.cmsfs.collection.impl

import akka.Done
import akka.stream.scaladsl.Flow
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.shinhwagk.config.api.ConfigService
import org.shinhwagk.query.api.{QueryOracleMessage, QueryService}
import org.wex.cmsfs.collection.api
import org.wex.cmsfs.collection.api.CollectionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HandleQueryWhileStartCollection(persistentEntityRegistry: PersistentEntityRegistry, collectinService: CollectionService, configService: ConfigService, queryService: QueryService) {
  def ref(id: String) = persistentEntityRegistry.refFor[CollectionEntity](id)
  collectinService.collectionEvents.subscribe.atLeastOnce(Flow[api.CollectionEvent].mapAsync(1) {
    case cs: api.CollectionCreated if cs.connectorId == 1 => {
      println(cs,10)
      for {
        monitor <- configService.getMonitorById(cs.monitorId).invoke()
        connector <- configService.getMachineConnectorModeJdbc(cs.connectorId).invoke()
        result <- queryService.queryForOracle("ARRAY").invoke(QueryOracleMessage(connector.jdbcUrl, connector.username, connector.password, monitor.dslCode, monitor.args))
        r <- {
          println(result)
          ref(cs.id.toString).ask(RecordCollection(result))
        }
      } yield r
    }
    case _=> Future(Done)
  })
  //
  //  def getConnector: ServiceCall[NotUsed, MachineConnectorModeJDBC] = {
  ////    configService.getMachineConnectorModeJdbc(c.connectorId)
  //  }
  //
  //  def getMonitor = {
  ////    configService.getMonitorById(c.monitorId)
  //    //    configService.getMonitor
  //  }


}
