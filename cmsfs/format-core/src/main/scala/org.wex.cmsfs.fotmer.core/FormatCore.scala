package org.wex.cmsfs.fotmer.core

import play.api.Configuration
import play.api.libs.json.Json

trait FormatCore {

  val config: Configuration

  val formatUrl: String = config.getString("format.url").get

  def genUrl(path: String): String = {
    formatUrl :: Json.parse(path).as[List[String]] mkString "/"
  }

}
