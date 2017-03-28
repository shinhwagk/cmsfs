package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreConnectorJdbc(id: Int, name: String, url: String, user: String, password: String)

object CoreConnectorJdbc {
  implicit val format: Format[CoreConnectorJdbc] = Json.format
}