package org.wex.cmsfs.config.impl


import akka.stream.Materializer
import org.wex.cmsfs.monitor.api.MonitorService

import scala.concurrent.ExecutionContext

class Testx(collectingService: MonitorService)(implicit ec: ExecutionContext, materializer: Materializer) {
  println(1)
//  collectingService.getCollectItem.invoke().foreach(_.runForeach(x => println(x, "9")))

}
