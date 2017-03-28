package org.wex.cmsfs.common.`object`

import play.api.libs.json.{Format, Json}

case class CoreConnectorSsh(id: Int, name: String, ip: String, user: String, password: Option[String], privateKey: Option[String])

object CoreConnectorSsh {
  implicit val format: Format[CoreConnectorSsh] = Json.format
}