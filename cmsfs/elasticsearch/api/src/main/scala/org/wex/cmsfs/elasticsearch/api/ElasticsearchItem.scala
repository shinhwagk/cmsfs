package org.wex.cmsfs.elasticsearch.api

import play.api.libs.json.{Format, Json}

case class ElasticsearchItem()

object ElasticsearchItem {
  implicit val format: Format[ElasticsearchItem] = Json.format
}