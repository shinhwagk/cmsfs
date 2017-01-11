package org.shinhwagk.config.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.config.api.{ConfigService, Host}
import org.shinhwagk.config.db.Tables
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext

/**
  * Implementation of the LagomhelloService.
  */
class ConfigServiceImpl(configService: ConfigService)(implicit ec: ExecutionContext) extends ConfigService {

  val db = Database.forConfig("oso-config")

  override def getNode(id: Int): ServiceCall[NotUsed, Host] = ServiceCall { _ =>
    db.run(Tables.hosts.filter(_.id === id).result.head)
  }

  override def getNodes: ServiceCall[NotUsed, List[Host]] = ServiceCall { _ =>
    db.run(Tables.hosts.result).map(_.toList)
  }

  override def deleteNode(id: Int): ServiceCall[NotUsed, NotUsed] = ServiceCall { _ =>
    db.run(Tables.hosts.filter(_.id === id).delete).map(_ => NotUsed)
  }

  override def addNode: ServiceCall[Host, NotUsed] = ServiceCall { p =>
    db.run(Tables.hosts += p).map(_ => NotUsed)
  }

  override def putNode(id: Int): ServiceCall[Host, NotUsed] = ServiceCall { p =>
    db.run(Tables.hosts.filter(_.id === id).update(p.copy(id = Some(id)))).map(_ => NotUsed)
  }
}
