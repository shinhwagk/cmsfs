package org.shinhwagk.query.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, ResponseHeader}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import org.shinhwagk.query.api.{QueryOSMessage, QueryOracleMessage, QueryService}
import org.slf4j.{ Logger, LoggerFactory }
import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global

class QueryServiceImpl extends QueryService {

  private final val logger: Logger =    LoggerFactory.getLogger(this.getClass)
  override def queryForOracle(mode: String) = ServerServiceCall { (_, qom) =>
    val responseHeader: ResponseHeader = ResponseHeader(200, MessageProtocol.empty, immutable.Seq.empty)
    qom.mode(mode).map(response => (responseHeader, response))
  }

  override def queryForOSScript: ServiceCall[QueryOSMessage, String] = ServerServiceCall { qom =>
    logger.info("query for os receive")
    qom.exec
  }
}