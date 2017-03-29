package org.wex.cmsfs.common.core

import play.api.Configuration
import play.api.libs.json.Json

import scala.io.Source

trait Common {

  val config: Configuration

  private val formatUrl: String = config.getString("cmsfs.url").get

  def getUrlByPath(path: String): String = {
    formatUrl :: Json.parse(path).as[List[String]] mkString "/"
  }

  def getUrlContentByPath(path: String): String = {
    Source.fromURL(getUrlByPath(path), "UTF-8").mkString.trim
  }
}
