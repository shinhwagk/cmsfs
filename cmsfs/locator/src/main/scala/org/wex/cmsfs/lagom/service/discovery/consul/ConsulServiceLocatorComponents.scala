package org.wex.cmsfs.lagom.service.discovery.consul

import java.net.{InetAddress, URI}
import java.util.concurrent.ThreadLocalRandom

import com.ecwid.consul.v1.catalog.model.CatalogService
import com.ecwid.consul.v1.{ConsulClient, QueryParams}
import com.lightbend.lagom.internal.client.CircuitBreakers
import com.lightbend.lagom.scaladsl.api.Descriptor.Call
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.client.{CircuitBreakerComponents, CircuitBreakingServiceLocator}
import org.slf4j.{Logger, LoggerFactory}
import play.api.Configuration

import scala.collection.JavaConverters._
import scala.collection.concurrent.{Map, TrieMap}
import scala.concurrent.{ExecutionContext, Future}

trait ConsulServiceLocatorComponents extends CircuitBreakerComponents {
  lazy val serviceLocator: ServiceLocator = new ConsulServiceLocator(configuration, circuitBreakers)(executionContext)
}

class ConsulServiceLocator(configuration: Configuration, circuitBreakers: CircuitBreakers)(implicit ec: ExecutionContext)
  extends CircuitBreakingServiceLocator(circuitBreakers) {

  private final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  lazy val client: ConsulClient = new ConsulClient("consul.cmsfs.org", 8500)

  private val roundRobinIndexFor: Map[String, Int] = TrieMap.empty[String, Int]

  override def locate(name: String, serviceCall: Call[_, _]): Future[Option[URI]] = Future {
    val instances: List[CatalogService] = client.getCatalogService(name, QueryParams.DEFAULT).getValue.asScala.toList
    instances.size match {
      case 0 => None
      case 1 => toURIs(instances).headOption
      case _ => Some(pickRoundRobinInstance(name, instances))
      //        throw new Exception("NOTHING")
      //        RoutingPolicy match {
      //          case First => Some(pickFirstInstance(instances))
      //          case Random => Some(pickRandomInstance(instances))
      //          case RoundRobin => Some(pickRoundRobinInstance(name, instances))
      //        }
    }
  }

  private[consul] def pickFirstInstance(services: List[CatalogService]): URI = {
    if (services.isEmpty) throw new IllegalStateException("List of services should not be empty")
    toURIs(services).sorted.head
  }

  private[consul] def pickRandomInstance(services: List[CatalogService]): URI = {
    if (services.isEmpty) throw new IllegalStateException("List of services should not be empty")
    toURIs(services).sorted.apply(ThreadLocalRandom.current.nextInt(services.size - 1))
  }

  private[consul] def pickRoundRobinInstance(name: String, services: List[CatalogService]): URI = {
    if (services.isEmpty) throw new IllegalStateException("List of services should not be empty")
    roundRobinIndexFor.putIfAbsent(name, 0)
    val sortedServices = toURIs(services).sorted
    val currentIndex = roundRobinIndexFor(name)
    val nextIndex =
      if (sortedServices.size > currentIndex + 1) currentIndex + 1
      else 0
    roundRobinIndexFor.replace(name, nextIndex)
    sortedServices.apply(currentIndex)
  }

  private def toURIs(services: List[CatalogService]): List[URI] =
    services.map { service =>
      val address = service.getServiceAddress
      val serviceAddress =
        if (address.trim.isEmpty || address == "localhost") InetAddress.getLoopbackAddress.getHostAddress
        else address
      //      new URI(s"${config.scheme}://$serviceAddress:${service.getServicePort}")
      new URI(s"http://$serviceAddress:${service.getServicePort}")
    }
}
