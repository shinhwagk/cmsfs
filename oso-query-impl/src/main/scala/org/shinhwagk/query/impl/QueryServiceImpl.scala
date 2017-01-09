package org.shinhwagk.query.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.shinhwagk.query.api.QueryService

import scala.concurrent.Future

/**
  * Implementation of the LagomhelloService.
  */
class QueryServiceImpl extends QueryService {

  override def query(id: Int) = ServiceCall { q =>
    Future.successful(s"hello,${q}");
  }
}
