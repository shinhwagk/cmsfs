package org.wex.cmsfs.lagom.service.discovery.name

import java.net.URI

import com.lightbend.lagom.internal.client.CircuitBreakers
import com.lightbend.lagom.scaladsl.api.Descriptor.Call
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.client.{CircuitBreakerComponents, CircuitBreakingServiceLocator}
import org.slf4j.{Logger, LoggerFactory}
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

trait ServiceNameServiceLocatorComponents extends CircuitBreakerComponents {
  lazy val serviceLocator: ServiceLocator = new ServiceNameServiceLocator(configuration, circuitBreakers)(executionContext)
}

class ServiceNameServiceLocator(configuration: Configuration, circuitBreakers: CircuitBreakers)(implicit ec: ExecutionContext)
  extends CircuitBreakingServiceLocator(circuitBreakers) {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private def getServicesByName(name: String): Option[URI] = {
    Some(URI.create("http://" + name + ".cmsfs.org:9000"))
  }

  override def locate(name: String, serviceCall: Call[_, _]) = {
    val uriOpt = getServicesByName(name)
    logger.info(uriOpt.toString + " request............")
    Future.successful(uriOpt)
  }
}