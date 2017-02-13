package org.wex.cmsfs.collecting.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.cmsfs.collecting.api.{CollectItem, CollectingService}

import scala.concurrent.ExecutionContext

class CollectingServiceImpl(registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends CollectingService {
  override def createCollectItem: ServiceCall[CollectItem, CollectItem] = ServiceCall { c =>
    entityRef(c.id).ask(CreateCollectItem(c)).map(_ => c)
  }

  private def entityRef(id: Int) = entityRefString(id.toString)

  private def entityRefString(id: String) = registry.refFor[CollectingEntity](id)
}
