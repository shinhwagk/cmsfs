package org.shinhwagk.alarm.api

import java.util.UUID

case class Alarm(AlarmId: UUID, script: String, args: List[String])
