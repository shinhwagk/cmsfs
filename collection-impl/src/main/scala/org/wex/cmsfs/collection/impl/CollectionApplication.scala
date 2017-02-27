package org.wex.cmsfs.collection.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import org.shinhwagk.query.api.QueryService
import org.wex.cmsfs.collection.api.{Collection, CollectionService}
import play.api.Environment
import play.api.libs.ws.ahc.AhcWSComponents

import scala.concurrent.ExecutionContext

abstract class CollectionApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with CollectionComponents
  with AhcWSComponents
  with LagomKafkaComponents {
  lazy val queryService = serviceClient.implement[QueryService]
  lazy val configService = serviceClient.implement[ConfigService]
  lazy val colectiongService = serviceClient.implement[CollectionService]

  wire[HandleQueryWhileStartCollection]
}

trait CollectionComponents extends LagomServerComponents
  with CassandraPersistenceComponents {

  implicit def executionContext: ExecutionContext

  def environment: Environment

  override lazy val lagomServer = LagomServer.forServices(
    bindService[CollectionService].to(wire[CollectionServiceImpl])
  )
  lazy val jsonSerializerRegistry = CollectionSerializerRegistry

  persistentEntityRegistry.register(wire[CollectionEntity])
  readSide.register(wire[CollectionEventProcessor])
  wire[CollectionItemRepository]

  //  def taxCalculator(taxBase: Collection) = wire[HandleQueryWhileStartCollection]
}


class CollectionApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new CollectionApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new CollectionApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
}
