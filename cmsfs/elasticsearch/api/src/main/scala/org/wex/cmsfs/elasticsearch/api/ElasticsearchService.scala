package org.wex.cmsfs.elasticsearch.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{CircuitBreaker, Service, ServiceCall}

object ElasticsearchService {
  val SERVICE_NAME = "elastic-search"
}

trait ElasticsearchService extends Service {

  def pushElasticsearchItem(_index: String, _type: String): ServiceCall[String, Done]

  override final def descriptor = {
    import ElasticsearchService._
    import Service._
    named(SERVICE_NAME).withCalls(
      pathCall("/v1/elasticsearch/:_index/:_type", pushElasticsearchItem _)
        .withCircuitBreaker(CircuitBreaker.identifiedBy("elasticsearch-circuitbreaker"))
    )
  }
}
