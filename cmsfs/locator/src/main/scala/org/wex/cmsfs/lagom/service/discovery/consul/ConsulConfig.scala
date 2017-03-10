package org.wex.cmsfs.lagom.service.discovery.consul

import com.typesafe.config.ConfigException.BadValue
import play.api.Configuration

case class ConsulConfig(agentHostname: String, agentPort: Int, scheme: String, routingPolicy: RoutingPolicy)

object ConsulConfig {
  def apply(config: Configuration): ConsulConfig = {
    val agentHostname = config.getString("lagom.discovery.consul.agent-hostname").get
    val agentPort = config.getInt("lagom.discovery.consul.agent-port").get
    val scheme = config.getString("lagom.discovery.consul.uri-scheme").get
    val routingPolicy = RoutingPolicy(config.getString("lagom.discovery.consul.routing-policy").get)
    new ConsulConfig(agentHostname, agentPort, scheme, routingPolicy)
  }
}

object RoutingPolicy {
  def apply(policy: String): RoutingPolicy = policy match {
    case "first" => First
    case "random" => Random
    case "round-robin" => RoundRobin
    case unknown => throw new BadValue("lagom.discovery.consul.routing-policy", s"[$unknown] is not a valid routing algorithm")
  }
}

sealed trait RoutingPolicy

case object First extends RoutingPolicy

case object Random extends RoutingPolicy

case object RoundRobin extends RoutingPolicy