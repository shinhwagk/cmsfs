package org.wex.cmsfs.collect.ssh.api

import org.wex.cmsfs.config.api.{CoreCollect, CoreConnectorSsh}
import play.api.libs.json.{Format, Json}

case class CollectItemSsh(monitorDetailId: Int, collect: CoreCollect, connector: CoreConnectorSsh, utcDate: String)

object CollectItemSsh {
  implicit val format: Format[CollectItemSsh] = Json.format
}