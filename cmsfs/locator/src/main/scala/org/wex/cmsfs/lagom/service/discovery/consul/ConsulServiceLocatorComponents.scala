package org.wex.cmsfs.lagom.service.discovery.consul

import java.net.URI

import com.lightbend.lagom.internal.client.CircuitBreakers
import com.lightbend.lagom.scaladsl.api.Descriptor.Call
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.client.{CircuitBreakerComponents, CircuitBreakingServiceLocator}
import org.slf4j.{Logger, LoggerFactory}
import play.api.Configuration
import scala.concurrent.{ExecutionContext, Future}

trait ConsulServiceLocatorComponents extends CircuitBreakerComponents {
  lazy val serviceLocator: ServiceLocator = new ConsulServiceLocator(configuration, circuitBreakers)(executionContext)
}

class ConsulServiceLocator(configuration: Configuration, circuitBreakers: CircuitBreakers)(implicit ec: ExecutionContext)
  extends CircuitBreakingServiceLocator(circuitBreakers) {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private val consulServiceExtract = new ConsulServiceExtract(configuration)

  override def locate(name: String, serviceCall: Call[_, _]): Future[Option[URI]] = Future {
    consulServiceExtract.getService(name)
  }
}
