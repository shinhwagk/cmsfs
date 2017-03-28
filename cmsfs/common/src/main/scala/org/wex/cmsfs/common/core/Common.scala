package org.wex.cmsfs.common.core

import play.api.Configuration
import play.api.libs.json.Json

import scala.io.Source

trait Common {

  val config: Configuration

  private val formatUrl: String = config.getString("cmsfs.url").get

  private def genUrl(path: String): String = {
    formatUrl :: Json.parse(path).as[List[String]] mkString "/"
  }

  def getUrlPathContent(path: String): String = {
    Source.fromURL(genUrl(path), "UTF-8").mkString
  }
}
