package org.shinhwagk.monitor.monitor

/**
  * Created by shinhwagk on 2017/1/22.
  */
trait ProcessAlarm {
  def query(qs: QueryService):Future[ProcessOriginal]
}
