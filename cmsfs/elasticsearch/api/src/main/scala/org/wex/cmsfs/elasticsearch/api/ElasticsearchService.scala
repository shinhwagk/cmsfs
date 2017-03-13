package org.wex.cmsfs.elasticsearch.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

object ElasticsearchService {
  val SERVICE_NAME = "elasticsearch"
}

trait ElasticsearchService extends Service {

  def pushElasticsearchItem(_index: String, _type: String, _id: Option[String] = None): ServiceCall[String, Done]

  override final def descriptor = {
    import ElasticsearchService._
    import Service._
    named(SERVICE_NAME).withCalls(
      pathCall("/v1/elasticsearch/:_index/:_type/:_id", pushElasticsearchItem _),
      pathCall("/v1/elasticsearch/:_index/:_type", pushElasticsearchItem _)
    )
  }
}
