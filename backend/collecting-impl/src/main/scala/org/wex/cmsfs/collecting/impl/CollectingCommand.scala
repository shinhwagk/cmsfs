//package org.wex.cmsfs.collecting.impl
//
//import akka.Done
//import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
//import org.cmsfs.collecting.api.CollectItem
//import play.api.libs.json.{Format, Json}
//
//
//sealed trait CollectingCommand
//
//case class CreateCollectItem(collecting:CollectItem) extends CollectingCommand with ReplyType[Done]
//
//object CreateCollectItem {
//  implicit val format: Format[CreateCollectItem] = Json.format
//}