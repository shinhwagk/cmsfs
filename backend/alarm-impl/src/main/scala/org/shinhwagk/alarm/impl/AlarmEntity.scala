package org.shinhwagk.alarm.impl

import java.util.UUID

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import org.shinhwagk.alarm.api.Alarm
import play.api.libs.json.{Format, Json}

/**
  * Created by shinhwagk on 2017/2/5.
  */
class AlarmEntity extends PersistentEntity {
  override type Command = AlarmCommand
  override type Event = AlarmEvent
  override type State = Option[Alarm]

  override def initialState = None

  override def behavior: Behavior = {
    case None => notCreated
    case Some(alarm) =>
  }

  private val notCreated = {
    Actions().onCommand[CreateAlarm, Done] {
      case (CreateAlarm(alarm), ctx, _) =>
        ctx.thenPersist(AlarmCreated(alarm))(_ => ctx.reply(Done))
    }.onEvent {
      case (AlarmCreated(alarm), _) => Some(alarm)
    }
  }
}

sealed trait AlarmCommand

case class CreateAlarm(alarm: Alarm) extends AlarmCommand with ReplyType[Done]

object CreateAlarm {
  implicit val format: Format[CreateAlarm] = Json.format
}

sealed trait AlarmEvent

case class AlarmCreated(alarm: Alarm) extends AlarmEvent

object AlarmCreated {
  implicit val format: Format[AlarmCreated] = Json.format
}
