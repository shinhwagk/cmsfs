package org.wex.cmsfs.common

import akka.stream.{ActorAttributes, Supervision}
import org.slf4j.Logger

trait CmsfsAkkaStream {

  val logger: Logger

  private def decider(f: (String) => String): Supervision.Decider = {
    case ex: Exception =>
      logger.error(f(ex.getMessage))
      Supervision.Resume
  }

  def supervisionStrategy(f: (String) => String) = {
    ActorAttributes.supervisionStrategy(decider(f))
  }

  def loggerFlow[T](elem: T, mess: String): T = {
    logger.info(mess)
    elem
  }

}
