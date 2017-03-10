package org.wex.cmsfs.lagom.service.discovery.consul

import java.net.{InetAddress, URI}
import java.util.concurrent.ThreadLocalRandom
import com.ecwid.consul.v1.catalog.model.CatalogService
import com.ecwid.consul.v1.{ConsulClient, QueryParams}
import play.api.Configuration
import scala.collection.JavaConverters._
import scala.collection.concurrent.{Map, TrieMap}

class ConsulServiceExtract(configuration: Configuration) {

  def getService(name: String): Option[URI] = {
    val instances: List[CatalogService] = client.getCatalogService(name, QueryParams.DEFAULT).getValue.asScala.toList
    instances.size match {
      case 0 => None
      case 1 => toURIs(instances).headOption
      case _ => consulConfig.routingPolicy match {
        case First => Some(pickFirstInstance(instances))
        case Random => Some(pickRandomInstance(instances))
        case RoundRobin => Some(pickRoundRobinInstance(name, instances))
      }
    }
  }

  private val roundRobinIndexFor: Map[String, Int] = TrieMap.empty[String, Int]

  private val consulConfig: ConsulConfig = ConsulConfig(configuration)

  lazy val client: ConsulClient = new ConsulClient("consul.cmsfs.org", consulConfig.agentPort)

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
    val sortedServices: Seq[URI] = toURIs(services).sorted
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
      new URI(s"${consulConfig.scheme}://$serviceAddress:${service.getServicePort}")
    }
}

