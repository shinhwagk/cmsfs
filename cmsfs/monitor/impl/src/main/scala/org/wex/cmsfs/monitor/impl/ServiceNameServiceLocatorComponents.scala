package org.wex.cmsfs.monitor.impl

import java.net.URI

import com.lightbend.lagom.internal.client.CircuitBreakers
import com.lightbend.lagom.scaladsl.api.Descriptor.Call
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.client.{CircuitBreakerComponents, CircuitBreakingServiceLocator}
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

trait ServiceNameServiceLocatorComponents extends CircuitBreakerComponents {
  lazy val serviceLocator: ServiceLocator = new ServiceNameServiceLocator(configuration, circuitBreakers)(executionContext)
}

class ServiceNameServiceLocator(configuration: Configuration, circuitBreakers: CircuitBreakers)(implicit ec: ExecutionContext)
  extends CircuitBreakingServiceLocator(circuitBreakers) {

  private def getServicesByName(name: String): Option[URI] = {
    Some(URI.create("http://" + name + ".cmsfs.org:9000"))
  }

  override def locate(name: String, serviceCall: Call[_, _]) = {
    Future.successful(getServicesByName(name))
  }
}