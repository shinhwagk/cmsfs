package org.shinhwagk.query.impl

import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, ResponseHeader}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import org.shinhwagk.query.api.QueryService
import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Implementation of the LagomhelloService.
  */
class QueryServiceImpl extends QueryService {

  override def query(id: Int, mode: String) = ServerServiceCall { (_, qom) =>
    val responseHeader: ResponseHeader = ResponseHeader(200, MessageProtocol.empty, immutable.Seq.empty)
    qom.mode(mode).map(response => (responseHeader, response))
  }
}
