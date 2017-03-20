package org.wex.cmsfs.fotmer.core

import play.api.Configuration
import play.api.libs.json.Json

trait FormatCore {

  val config: Configuration

  def genUrl(path: String): String = {
    val formatUrl = config.getString("format.url").get
    formatUrl :: Json.parse(path).as[Seq[String]] :: Nil mkString "/"
  }

}
