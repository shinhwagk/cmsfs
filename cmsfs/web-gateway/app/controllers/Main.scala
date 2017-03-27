package controllers

import org.wex.cmsfs.monitor.status.impl.MonitorStatusService
import play.api.mvc.{Action, Controller}
import scala.concurrent.{ExecutionContext, Future}

class Main(monitorStatusService: MonitorStatusService)(implicit ec: ExecutionContext) extends Controller {
  def test = Action.async { implicit rh =>
    Future.successful(Ok("aaa"))
  }
}
