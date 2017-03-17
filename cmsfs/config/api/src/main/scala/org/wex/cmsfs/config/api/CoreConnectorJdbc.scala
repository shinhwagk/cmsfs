package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreConnectorJdbc(id: Option[Int], name: String, url: String, user: String, password: String)

object CoreConnectorJdbc extends ((Option[Int], String, String, String, String) => CoreConnectorJdbc) {
  implicit val format: Format[CoreConnectorJdbc] = Json.format
}