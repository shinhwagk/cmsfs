import scala.language.postfixOps

organization in ThisBuild := "org.wex"
version in ThisBuild := "1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.8"

//val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "3.3"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val slick = "com.typesafe.slick" %% "slick" % "3.2.0"
val mysqlJdbc = "mysql" % "mysql-connector-java" % "6.0.5"
val commonIO = "commons-io" % "commons-io" % "2.5"
val quartz = "org.quartz-scheduler" % "quartz" % "2.2.3"
val jsch = "com.jcraft" % "jsch" % "0.1.54"
val consul = "com.ecwid.consul" % "consul-api" % "1.2.1"

lazy val root = (project in file("."))
  .aggregate(
    `monitor-api`, `monitor-impl`,
    `config-api`, `config-impl`,
    `collect-ssh-api`, `collect-ssh-impl`,
    `collect-jdbc-api`, `collect-jdbc-impl`,
    `format-analyze-api`, `format-analyze-impl`,
    //    `monitor-status-api`, `monitor-status-impl`,
    `lagom-service-locator`,
    `elasticsearch-api`, `web-gateway`
  )

def implCommonSettings: Seq[Setting[_]] = Seq(
  libraryDependencies ++= Seq(lagomScaladslTestKit, macwire, scalaTest)
)

lazy val `config-api` = (project in file("config/api"))
  .settings(libraryDependencies += lagomScaladslApi)
lazy val `config-impl` = (project in file("config/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies ++= Seq(mysqlJdbc, slick))
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`config-api`, `common`, `lagom-service-locator`)

//lazy val `bootstrap` = (project in file("bootstrap"))
//  .enablePlugins(LagomScala)
//  .settings(libraryDependencies += lagomScaladslPubSub)
//  .settings(libraryDependencies ++= Seq(quartz))
//  .settings(implCommonSettings: _*)
//  .settings(lagomForkedTestSettings: _*)
//  .dependsOn(`common`, `config-api`,
//    `collect-ssh-api`, `collect-jdbc-api`,
//    `format-analyze-api`, `format-alarm-api`,
//    `lagom-service-locator`)

lazy val `monitor-api` = (project in file("monitor/api"))
  .settings(libraryDependencies += lagomScaladslApi)
lazy val `monitor-impl` = (project in file("monitor/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)
  .settings(libraryDependencies ++= Seq(quartz))
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`monitor-api`, `common`, `config-api`, `collect-ssh-api`, `collect-jdbc-api`, `lagom-service-locator`)

lazy val `collect-ssh-api` = (project in file("collect-ssh/api"))
  .settings(libraryDependencies += lagomScaladslApi)
  .dependsOn(`common`)
lazy val `collect-ssh-impl` = (project in file("collect-ssh/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)
  .settings(libraryDependencies ++= Seq(jsch))
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`collect-ssh-api`, `format-alarm-api`, `format-analyze-api`, `lagom-service-locator`, `monitor-status-api`)

lazy val `collect-jdbc-api` = (project in file("collect-jdbc/api"))
  .settings(libraryDependencies += lagomScaladslApi)
  .dependsOn(`common`)
lazy val `collect-jdbc-impl` = (project in file("collect-jdbc/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)
  //  .settings(libraryDependencies ++= Seq(mysqlJdbc))
  .settings(libraryDependencies += "com.oracle" % "jdbc" % "8" from "file:///" + baseDirectory.value / ".." / "jars" / "ojdbc8.jar")
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`collect-jdbc-api`, `format-alarm-api`, `format-analyze-api`, `lagom-service-locator`, `monitor-status-api`)

lazy val `elasticsearch-api` = (project in file("elasticsearch/api"))
  .settings(libraryDependencies += lagomScaladslApi)
lagomUnmanagedServices in ThisBuild += ("elastic-search" -> "http://elasticsearch.cmsfs.org:9200")
lazy val `format-analyze-api` = (project in file("format-analyze/api"))
  .settings(libraryDependencies += lagomScaladslApi)
  .dependsOn(`common`)
lazy val `format-analyze-impl` = (project in file("format-analyze/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`format-analyze-api`, `elasticsearch-api`, `lagom-service-locator`, `monitor-status-api`)

lazy val `format-alarm-api` = (project in file("format-alarm/api"))
  .settings(libraryDependencies += lagomScaladslApi)
  .dependsOn(`common`)
lazy val `format-alarm-impl` = (project in file("format-alarm/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)

  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`format-alarm-api`, `notification-api`, `lagom-service-locator`, `monitor-status-api`)

lazy val `notification-api` = (project in file("notification/api"))
  .settings(libraryDependencies += lagomScaladslApi)
lazy val `notification-impl` = (project in file("notification/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`notification-api`, `lagom-service-locator`)

lazy val `monitor-status-api` = (project in file("monitor-status/api"))
  .settings(libraryDependencies += lagomScaladslApi)
lazy val `monitor-status-impl` = (project in file("monitor-status/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`monitor-status-api`, `common`, `lagom-service-locator`)

lazy val `web-gateway` = (project in file("web-gateway"))
  .enablePlugins(PlayScala && LagomPlay)
  .dependsOn(`monitor-status-api`, `lagom-service-locator`)
  .settings(libraryDependencies ++= Seq(lagomScaladslServer, macwire, scalaTest))

lazy val `lagom-service-locator` = (project in file("locator"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += consul)

lazy val `common` = (project in file("common"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies ++= Seq(commonIO))
  .dependsOn(`config-api`)

//val Success = 0 // 0 exit code
//val Error = 1 // 1 exit code
//
//lazy val `ui-prod-build` = TaskKey[Unit]("Run UI build when packaging the application.")
//

//
//def runProdBuild(implicit dir: File): Int = ifUiInstalled(runScript("npm run build-prod"))
//
//def ifUiInstalled(task: => Int)(implicit dir: File): Int =
//  if (runNpmInstall == Success) task
//  else Error
//
//def runScript(script: String)(implicit dir: File): Int = Process(script, dir) !
//
//def runNpmInstall(implicit dir: File): Int =
//  if (uiWasInstalled) Success else runScript("npm install")
//
//def uiWasInstalled(implicit dir: File): Boolean = (dir / "node_modules").exists()
//

lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false
lagomCassandraCleanOnStart in ThisBuild := false
lagomServiceLocatorEnabled in ThisBuild := false

parallelExecution in ThisBuild := true
testForkedParallel in ThisBuild := true

fork in run := true