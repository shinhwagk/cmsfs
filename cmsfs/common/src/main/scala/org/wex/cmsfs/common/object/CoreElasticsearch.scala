package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreElasticsearch(_index: String, _type: String, _id: Option[String] = None)

object CoreElasticsearch {
  implicit val format: Format[CoreElasticsearch] = Json.format
}
