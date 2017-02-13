package org.wex.cmsfs.collecting.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.cmsfs.collecting.api.CollectingService
import org.shinhwagk.config.api.ConfigService
import org.shinhwagk.query.api.QueryService
import play.api.libs.ws.ahc.AhcWSComponents

class CollectingApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MonitorApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new MonitorApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
}

abstract class MonitorApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with PubSubComponents {

  override lazy val lagomServer = LagomServer.forServices(
    bindService[CollectingService].to(wire[CollectingServiceImpl])
  )

  override lazy val jsonSerializerRegistry = CollectingSerializerRegistry

  persistentEntityRegistry.register(wire[CollectingEntity])

  val configService = serviceClient.implement[ConfigService]
  val queryService = serviceClient.implement[QueryService]

  wire[CollectingAction]
}