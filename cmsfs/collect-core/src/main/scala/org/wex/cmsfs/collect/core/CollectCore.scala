package org.wex.cmsfs.collect.core

import play.api.Configuration
import play.api.libs.json.Json

trait CollectCore {

  val config: Configuration

  private val formatUrl: String = config.getString("collect.url").get

  def genUrl(path: String): String = {
    formatUrl :: Json.parse(path).as[List[String]] mkString "/"
  }
}
