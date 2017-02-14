package org.wex.cmsfs.config.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.wex.cmsfs.config.api.MonitorModeJDBC

import scala.collection.immutable.Seq

/**
  * Created by shinhwagk on 2017/2/3.
  */
object ConfigSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[MonitorModeJDBC]
  )
}
