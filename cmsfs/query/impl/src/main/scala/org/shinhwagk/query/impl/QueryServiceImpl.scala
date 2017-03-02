package org.shinhwagk.query.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, ResponseHeader}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import org.shinhwagk.query.api.{QueryOSMessage, QueryOracleMessage, QueryService}

import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Implementation of the LagomhelloService.
  */
class QueryServiceImpl extends QueryService {

  override def queryForOracle(mode: String) = ServerServiceCall { (_, qom) =>
    val responseHeader: ResponseHeader = ResponseHeader(200, MessageProtocol.empty, immutable.Seq.empty)
    qom.mode(mode).map(response => (responseHeader, response))
  }

  override def queryForOSScript: ServiceCall[QueryOSMessage, String] = ServerServiceCall { qom =>
    qom.exec
  }
}