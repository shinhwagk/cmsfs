package org.wex.cmsfs.notification.impl

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.ws.{WSClient, WSRequest}
import scala.util.{Failure, Success}

class NotificationAction(nt: NotificationTopic,
                         system: ActorSystem,
                         ws: WSClient)(implicit mat: Materializer) {

  private implicit val executionContext = system.dispatcher

  try {
    val request: WSRequest = ws.url("http://10.65.209.12:8380/services/rest/msgNotify")
    request.post(Map("key" -> Seq("value"))).onComplete {
      case Success(a) => println(a + " success")
      case Failure(ex) => println(ex.getMessage + " re")
    }
  } catch {
    case ex: Exception => println(ex.getMessage + " x")
  }
}

