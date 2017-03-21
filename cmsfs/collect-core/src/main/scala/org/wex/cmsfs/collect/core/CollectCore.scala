package org.wex.cmsfs.collect.core

import play.api.Configuration
import play.api.libs.json.Json

trait CollectCore {
  val config: Configuration

  def genUrl(path: String): String = {
    val formatUrl: String = config.getString("collect.url").get
    formatUrl :: Json.parse(path).as[List[String]] mkString "/"
  }
}
