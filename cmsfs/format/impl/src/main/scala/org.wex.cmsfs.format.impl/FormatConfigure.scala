package org.wex.cmsfs.format.impl

import com.typesafe.config.ConfigFactory

object FormatConfigure {
  val formatUrl  = ConfigFactory.load().getString("format.url")
}
