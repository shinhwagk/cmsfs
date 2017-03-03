organization in ThisBuild := "org.wex"
version in ThisBuild := "1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.8"

val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "3.3"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val slick = "com.typesafe.play" %% "play-slick" % "2.0.0"

lazy val root = (project in file("."))
  .aggregate(
    `query-api`, `query-impl`,
    `config-api`, `config-impl`,
    `monitor-api`, `monitor-impl`,
    `format-api`, `format-impl`
  )

lazy val `query-api` = (project in file("query/api"))
  .settings(
    resolvers ++= Seq("Spray Repository" at "http://dev.rtmsoft.me/nexus/content/groups/public/"),
    libraryDependencies ++= Seq(
      "com.wingtech" % "ojdbc" % "7",
      "com.jcraft" % "jsch" % "0.1.54",
      lagomScaladslApi
    )
  )

lazy val `query-impl` = (project in file("query/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    ))
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`query-api`)
//
lazy val `config-api` = (project in file("config/api"))
  .settings(
    libraryDependencies += lagomScaladslApi
  )

lazy val `config-impl` = (project in file("config/impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      "mysql" % "mysql-connector-java" % "6.0.5",
      slick,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
    //    , lagomServicePort := 11000
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`config-api`, `monitor-api`)

//lazy val `alarm-api` = (project in file("alarm-api"))
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslApi
//    )
//  )
//
//lazy val `alarm-impl` = (project in file("alarm-impl"))
//  .enablePlugins(LagomScala)
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslTestKit, lagomScaladslPubSub, macwire, scalaTest
//    )
//  )
//  .settings(lagomForkedTestSettings: _*)
//  .dependsOn(`alarm-api`)

lazy val `format-api` = (project in file("format/api"))
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomScaladslApi
  )

lazy val `format-impl` = (project in file("format/impl"))
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      lagomScaladslPubSub,
      lagomLogback,
      "commons-io" % "commons-io" % "2.5",
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`format-api`, `config-api`)


lazy val `monitor-api` = (project in file("monitor/api"))
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomScaladslApi
  )

lazy val `monitor-impl` = (project in file("monitor/impl"))
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      //      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      //      lagomScaladslKafkaBroker,
      lagomScaladslPubSub,
      //      "com.datastax.cassandra" % "cassandra-driver-extras" % "3.0.0",
      "org.quartz-scheduler" % "quartz" % "2.2.3",
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`monitor-api`, `config-api`, `query-api`, `format-api`)

//lagomCassandraCleanOnStart in ThisBuild := false
lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false
//lagomKafkaAddress in ThisBuild := "10.65.103.58:9092"

fork in run := true