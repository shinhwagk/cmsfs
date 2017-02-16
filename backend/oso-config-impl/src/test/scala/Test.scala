import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpec}
import org.wex.cmsfs.config.api.ConfigService
import org.wex.cmsfs.config.impl.ConfigApplication

import scala.concurrent.ExecutionContext.Implicits.global

class Test extends WordSpec with Matchers with BeforeAndAfterAll with OptionValues {
  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup) { ctx =>
    new ConfigApplication(ctx) with LocalServiceLocator
  }

  lazy val client = server.serviceClient.implement[ConfigService]

  "The ConfigService" should {
    "getMonitorDetails" in {
      client.getMonitorDetails.invoke().map { response =>
        response.length >= 1
      }
    }
  }
}

