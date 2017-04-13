package org.wex.cmsfs.format.analyze.impl

import org.wex.cmsfs.common.core.CmsfsPlayJson
import play.api.libs.json.JsValue

trait FormatAnalyzeCore extends CmsfsPlayJson {

  def formatResultAddFiled(rs: Seq[JsValue], utcDate: String, metric: String): Seq[String] = {
    rs.map(jsonObjectAddField(_, "@timestamp", utcDate))
      .map(jsonObjectAddField(_, "@metric", metric))
      .map(_.toString())
  }

}
