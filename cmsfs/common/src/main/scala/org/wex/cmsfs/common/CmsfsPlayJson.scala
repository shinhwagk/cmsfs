package org.wex.cmsfs.common

import play.api.libs.json.{JsObject, JsString, JsValue}

trait CmsfsPlayJson {
  def jsonObjectAddField(json: JsValue, key: String, fieldVal: String): JsValue = {
    json.as[JsObject] + (key -> JsString(fieldVal))
  }
}