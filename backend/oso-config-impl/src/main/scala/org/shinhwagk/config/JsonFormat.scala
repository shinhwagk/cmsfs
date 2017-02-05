package org.shinhwagk.config

import play.api.libs.json._

/**
  * Created by shinhwagk on 2017/2/3.
  */
object JsonFormat {
  def toJsArray(ls: Seq[Any]): Seq[JsValue] = {
    ls.map(
      _ match {
        case i: Int => JsNumber(i)
        case b: Boolean if b => JsBoolean(true)
        case b: Boolean if !b => JsBoolean(false)
        case s: String => JsString(s)
      }
    )
  }
}
