package controllers

import ogr.wex.cmsfs.monitor.api.MonitorService
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

class Main(monitorStatusService: MonitorService)(implicit ec: ExecutionContext) extends Controller {
  def test = Action.async { implicit rh =>
    Future.successful(Ok("aaa"))
  }
}
