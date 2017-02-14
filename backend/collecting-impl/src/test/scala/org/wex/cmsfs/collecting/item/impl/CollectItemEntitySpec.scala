package org.wex.cmsfs.collecting.item.impl

import akka.actor.ActorSystem
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpec}
import org.wex.cmsfs.collecting.api.CollectItem
import org.wex.cmsfs.collecting.impl._

class CollectItemEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll with OptionValues {
  private val system = ActorSystem("test", JsonSerializerRegistry.actorSystemSetupFor(CollectingSerializerRegistry))

  private val item = CollectItem(1)
//
//  private def withDriver[T](block: PersistentEntityTestDriver[CollectingCommand, CollectingEvent, Option[CollectItem]] => T): T = {
//    val driver = new PersistentEntityTestDriver(system, new CollectingEntity, item.id.toString)
//    try {
//      block(driver)
//    } finally {
//      driver.getAllIssues shouldBe empty
//    }
//  }
//
//  "The item entity" should {
//    "allow creating an item" in withDriver { driver =>
//      val outcome = driver.run(CreateCollectItem(item))
//      outcome.events should contain only CollectItemCreated(item)
//      outcome.state should ===(Some(item))
//    }
//  }

}