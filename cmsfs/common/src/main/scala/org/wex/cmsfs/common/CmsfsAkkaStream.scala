package org.wex.cmsfs.common

import akka.stream.{ActorAttributes, Supervision}
import org.slf4j.Logger

object CmsfsAkkaStream {

  def decider(logger: Logger, f: (String) => String): Supervision.Decider = {
    case ex: Exception =>
      logger.error(f(ex.getMessage))
      Supervision.Resume
  }

  def supervisionStrategy(logger: Logger)(f: (String) => String) = {
    ActorAttributes.supervisionStrategy(decider(logger, f))
  }

  def loggerFlow[T](logger: Logger)(elem: T, mess: String): T = {
    logger.info(mess)
    elem
  }
}

trait CmsfsAkka {

  val logger: Logger

  def decider(f: (String) => String): Supervision.Decider = {
    case ex: Exception =>
      logger.error(f(ex.getMessage))
      Supervision.Resume
  }

  def supervisionStrategy(f: (String) => String) = {
    ActorAttributes.supervisionStrategy(decider(logger, f))
  }

  def loggerFlow[T](elem: T, mess: String): T = {
    logger.info(mess)
    elem
  }

}
