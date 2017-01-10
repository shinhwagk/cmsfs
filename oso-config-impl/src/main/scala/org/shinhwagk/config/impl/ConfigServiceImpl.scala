package org.shinhwagk.config.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.config.api.{ConfigService, Host}
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

/**
  * Implementation of the LagomhelloService.
  */
class ConfigServiceImpl extends ConfigService {

  val db = Database.forConfig("mysql")
  //  import dbConfig.driver.api._

  //  override def getNode(id: Int) = ServerServiceCall { _ =>
  //    val responseHeader: ResponseHeader = ResponseHeader(200, MessageProtocol.empty, immutable.Seq.empty)
  //    Future.successful((responseHeader, "[1,2,3]"))
  //  }
  //
  //  override def queryForOSScript: ServiceCall[QueryOSMessage, String] = ServerServiceCall { qom =>
  //    qom.exec
  //  }
//  override def getNodes: ServiceCall[NotUsed, List[Host]] = ???

  override def getNode(id: Int): ServiceCall[NotUsed, Host] = ServiceCall { _ =>

//    val resultingUsers = dbConfig.db.run(Tables.hosts.filter(_.id === id).result.head)
    //    val responseHeader: ResponseHeader = ResponseHeader(200, MessageProtocol.empty, immutable.Seq.empty)
    Future.successful(Host(1, "a", "a", 1, List("S"), true))
  }

//  override def getRDatabase(id: Int): ServiceCall[NotUsed, RDatabase] = ???
}
