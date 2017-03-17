package org.wex.cmsfs.config.api

import play.api.libs.json.{Format, Json}

case class CoreConnectorSsh(id: Option[Int], name: String, ip: String, port: Int, user: String, password: Option[String], privateKey: Option[String])

object CoreConnectorSsh extends ((Option[Int], String, String, Int, String, Option[String], Option[String]) => CoreConnectorSsh) {
  implicit val format: Format[CoreConnectorSsh] = Json.format
}