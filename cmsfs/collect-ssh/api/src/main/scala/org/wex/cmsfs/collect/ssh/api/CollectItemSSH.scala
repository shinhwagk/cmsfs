package org.wex.cmsfs.collect.ssh.api

import play.api.libs.json.{Format, Json}

case class CollectItemSSH(id: Int,
                          metricName: String,
                          args: Option[Seq[String]],
                          host: String,
                          port: Int,
                          user: String,
                          password: Option[String],
                          privateKey: Option[String],
                          utcDate: String,
                          name: String)

object CollectItemSSH {
  implicit val format: Format[CollectItemSSH] = Json.format
}