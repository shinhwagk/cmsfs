package org.wex.cmsfs.collect.jdbc.api

import play.api.libs.json.{Format, Json}

case class CollectItemJDBC(id: Int,
                           metricName: String,
                           args: Option[Seq[String]],
                           url: String,
                           user: String,
                           password: String)

object CollectItemJDBC {
  implicit val format: Format[CollectItemJDBC] = Json.format
}