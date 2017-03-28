package org.wex.cmsfs.collect.jdbc.api

import org.wex.cmsfs.common.`object`.{CoreCollect, CoreConnectorJdbc}
import play.api.libs.json.{Format, Json}

case class CollectItemJdbc(monitorDetailId: Int, collect: CoreCollect, connector: CoreConnectorJdbc, utcDate: String)

object CollectItemJdbc {
  implicit val format: Format[CollectItemJdbc] = Json.format
}