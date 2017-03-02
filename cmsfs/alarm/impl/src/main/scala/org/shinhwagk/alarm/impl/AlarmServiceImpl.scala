package org.shinhwagk.alarm.impl

import java.io.{File, PrintWriter}
import java.util.UUID

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.shinhwagk.alarm.api.{Alarm, AlarmService}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext
import scala.sys.process._

/**
  * Implementation of the LagomhelloService.
  */
class AlarmServiceImpl(configService: ConfigService, registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends AlarmService {
  override def getAlarmResult(id: Int): ServiceCall[List[String], JsValue] = ServiceCall { _ =>
    configService.getAlarm(id).invoke().map(alarm => {
      println(alarm)
      val pw = new PrintWriter(new File("hello.ps1"))
      pw.write(alarm.script)
      pw.close
    }).map(_ => s"powershell -File hello.ps1".!!).map(Json.parse _)
  }

  override def createAlarm: ServiceCall[Alarm, Alarm] = ServiceCall { alarm =>
    entityRef(alarm.AlarmId).ask(CreateAlarm(alarm)).map(_ => alarm)
  }

  private def entityRef(itemId: UUID) = entityRefString(itemId.toString)

  private def entityRefString(itemId: String) = registry.refFor[AlarmEntity](itemId)
}