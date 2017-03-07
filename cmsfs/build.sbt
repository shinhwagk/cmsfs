organization in ThisBuild := "org.wex"
version in ThisBuild := "1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.8"
lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false
lagomCassandraCleanOnStart in ThisBuild := false

//val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "3.3"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val slick = "com.typesafe.slick" %% "slick" % "3.2.0"
val mysqlJdbc = "mysql" % "mysql-connector-java" % "6.0.5"
val commonIO = "commons-io" % "commons-io" % "2.5"
val quartz = "org.quartz-scheduler" % "quartz" % "2.2.3"
val jsch = "com.jcraft" % "jsch" % "0.1.54"


lazy val root = (project in file("."))
  .aggregate(
    `config-api`, `config-impl`,
    `monitor-api`, `monitor-impl`,
    `collect-ssh-api`, `collect-ssh-impl`
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
  .dependsOn(`config-api`, `monitor-api`)
//  .settings(sampleStringTask := {
//    println(baseDirectory)
//  })

//lazy val `format-api` = (project in file("format/api"))
//  .settings(libraryDependencies += lagomScaladslApi)
//lazy val `format-impl` = (project in file("format/impl"))
//  .enablePlugins(LagomScala)
//  .settings(libraryDependencies ++= Seq(lagomScaladslPubSub, lagomLogback, commonIO))
//  .settings(implCommonSettings: _*)
//  .settings(lagomForkedTestSettings: _*)
//  .dependsOn(`format-api`, `config-api`)

lazy val `monitor-api` = (project in file("monitor/api"))
  .settings(libraryDependencies += lagomScaladslApi)
lazy val `monitor-impl` = (project in file("monitor/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)
  .settings(libraryDependencies ++= Seq(quartz))
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`monitor-api`, `config-api`, `collect-ssh-api`)

lazy val `collect-ssh-api` = (project in file("collect-ssh/api"))
  .settings(libraryDependencies += lagomScaladslApi)
lazy val `collect-ssh-impl` = (project in file("collect-ssh/impl"))
  .enablePlugins(LagomScala)
  .settings(libraryDependencies += lagomScaladslPubSub)
  .settings(libraryDependencies ++= Seq(jsch))
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`collect-ssh-api`, `monitor-api`)

lazy val `collect-jdbc-api` = (project in file("collect-jdbc/api"))
  .settings(libraryDependencies += lagomScaladslApi)
lazy val `collect-jdbc-impl` = (project in file("collect-jdbc/impl"))
  .enablePlugins(LagomScala)
  //  .settings(resolvers += "Spray Repository" at "http://dev.rtmsoft.me/nexus/content/groups/public/")
  .settings(libraryDependencies += lagomScaladslPubSub)
  .settings(libraryDependencies ++= Seq(mysqlJdbc))
  .settings(libraryDependencies += "com.wingtech" % "ojdbc" % "8" from "file:///" + baseDirectory.value / ".." / "jars" / "ojdbc8.jar")
  .settings(implCommonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`collect-jdbc-api`, `monitor-api`)

fork in run := true