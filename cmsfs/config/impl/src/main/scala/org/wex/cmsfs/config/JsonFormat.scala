package org.wex.cmsfs.config

import play.api.libs.json._

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
