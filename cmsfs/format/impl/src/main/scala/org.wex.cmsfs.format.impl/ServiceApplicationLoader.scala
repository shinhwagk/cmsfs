package org.wex.cmsfs.format.impl

import java.net.{URI, URISyntaxException}

import com.lightbend.lagom.internal.client.CircuitBreakers
import com.lightbend.lagom.scaladsl.api.Descriptor.Call
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.client.{CircuitBreakerComponents, CircuitBreakingServiceLocator, ConfigurationServiceLocatorComponents}
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import com.typesafe.config.ConfigException
import org.wex.cmsfs.config.api.ConfigService
import org.wex.cmsfs.format.api.FormatService
import org.wex.cmsfs.format.impl.action.{AlarmAction, AnalyzeAction}
import play.api.Configuration
import play.api.libs.ws.ahc.AhcWSComponents

import scala.concurrent.{ExecutionContext, Future}

class ServiceApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ServiceApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new ServiceApplication(context) with ConfigurationServiceLocatorComponents
}

abstract class ServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with PubSubComponents {

  val configService = serviceClient.implement[ConfigService]

  override lazy val lagomServer = LagomServer.forServices(
    bindService[FormatService].to(wire[FormatServiceImpl])
  )

  val formatTopic = wire[FormatTopic]
  val formatAction = wire[AlarmAction]
  val analyzeAction = wire[AnalyzeAction]

}

//trait ConfigurationServiceLocatorComponents extends CircuitBreakerComponents {
//  lazy val serviceLocator: ServiceLocator = new ConfigurationServiceLocator(configuration, circuitBreakers)(executionContext)
//}
//
//class ConfigurationServiceLocator(configuration: Configuration, circuitBreakers: CircuitBreakers)(implicit ec: ExecutionContext)
//  extends CircuitBreakingServiceLocator(circuitBreakers) {
//
//  private val ConsulService:String = "consul.cmsfs.org"
////  private val LagomServicesKey: String = "lagom.services"
//
//  private val services = {
//    if (configuration.underlying.hasPath(LagomServicesKey)) {
//      val config = configuration.underlying.getConfig(LagomServicesKey)
//      import scala.collection.JavaConverters._
//      (for {
//        key <- config.root.keySet.asScala
//      } yield {
//        try {
//          key -> URI.create(config.getString(key))
//        } catch {
//          case e: ConfigException.WrongType =>
//            throw new IllegalStateException(s"Error loading configuration for ConfigurationServiceLocator. Expected lagom.services.$key to be a String, but was ${config.getValue(key).valueType}", e)
//          case e: URISyntaxException =>
//            throw new IllegalStateException(s"Error loading configuration for ConfigurationServiceLocator. Expected lagom.services.$key to be a URI, but it failed to parse", e)
//        }
//      }).toMap
//    } else {
//      Map.empty[String, URI]
//    }
//  }
//
//  override def locate(name: String, serviceCall: Call[_, _]) = {
//    Future.successful(services.get(name))
//  }
//}