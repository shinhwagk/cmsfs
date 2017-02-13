organization in ThisBuild := "org.wex"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "3.3"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `oso` = (project in file("."))
  .aggregate(
    //    `oso-query-api`, `oso-query-impl`,
    `oso-config-api`, `oso-config-impl`,
    `collecting-api`, `collecting-impl`
    //    ,
    //    alarmApi, alarmImpl,
    //    `oso-monitor-api`, `oso-monitor-impl`,
    //    `oso-monitor-slave-api`, `oso-monitor-slave-impl`,
    //    `oso-monitor-alarm-api`, `oso-monitor-alarm-impl`,
    //    collectionApi, collectionImpl
    //    collectingApi, collectingImpl
  )
//
//lazy val `oso-query-api` = (project in file("oso-query-api"))
//  .settings(
//    resolvers ++= Seq("Spray Repository" at "http://dev.rtmsoft.me/nexus/content/groups/public/"),
//    libraryDependencies ++= Seq(
//      "com.wingtech" % "ojdbc" % "7",
//      "com.jcraft" % "jsch" % "0.1.54",
//      lagomScaladslApi
//    )
//  )
//
//lazy val `oso-query-impl` = (project in file("oso-query-impl"))
//  .enablePlugins(LagomScala)
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslTestKit,
//      macwire,
//      scalaTest
//    )
//  )
//  .settings(lagomForkedTestSettings: _*)
//  .dependsOn(`oso-query-api`)
//
lazy val `oso-config-api` = (project in file("oso-config-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `oso-config-impl` = (project in file("oso-config-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      "mysql" % "mysql-connector-java" % "6.0.5",
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`oso-config-api`)
//
//lazy val `oso-monitor-slave-api` = (project in file("oso-monitor-slave-api"))
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslApi
//    )
//  )
//
//lazy val `oso-monitor-slave-impl` = (project in file("oso-monitor-slave-impl"))
//  .enablePlugins(LagomScala)
//  .settings(
//    libraryDependencies ++= Seq(
//      "mysql" % "mysql-connector-java" % "6.0.5",
//      "org.quartz-scheduler" % "quartz" % "2.2.3",
//      lagomScaladslTestKit,
//      macwire,
//      scalaTest
//    )
//  )
//  .settings(lagomForkedTestSettings: _*)
//  .dependsOn(`oso-monitor-api`, `oso-config-api`)
//
//lazy val `oso-monitor-api` = (project in file("oso-monitor-api"))
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslApi
//    )
//  )
//
//lazy val `oso-monitor-impl` = (project in file("oso-monitor-impl"))
//  .enablePlugins(LagomScala)
//  .settings(
//    libraryDependencies ++= Seq(
//      "mysql" % "mysql-connector-java" % "6.0.5",
//      "com.typesafe.slick" %% "slick" % "3.1.1",
//      "org.quartz-scheduler" % "quartz" % "2.2.3",
//      lagomScaladslTestKit,
//      macwire,
//      scalaTest
//    )
//  )
//  .settings(lagomForkedTestSettings: _*)
//  .dependsOn(`oso-monitor-api`, `oso-config-api`, `oso-monitor-slave-api`, `oso-query-api`)
//
//lazy val alarmApi = (project in file("alarm-api"))
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslApi,
//      playJsonDerivedCodecs
//    )
//  )
//
//lazy val alarmImpl = (project in file("alarm-impl"))
//  .enablePlugins(LagomScala)
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslPersistenceCassandra,
//      lagomScaladslTestKit,
//      "mysql" % "mysql-connector-java" % "6.0.5",
//      macwire,
//      scalaTest
//    )
//  )
//  .settings(lagomForkedTestSettings: _*)
//  .dependsOn(collectionApi, alarmApi, `oso-config-api`)

lazy val `collecting-api` = (project in file("collecting-api"))
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )

lazy val `collecting-impl` = (project in file("collecting-impl"))
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      lagomScaladslKafkaBroker,
      lagomScaladslPubSub,
      "com.datastax.cassandra" % "cassandra-driver-extras" % "3.0.0",
      "org.quartz-scheduler" % "quartz" % "2.2.3",
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`collecting-api`, `oso-config-api`)

//lagomCassandraCleanOnStart in ThisBuild := false
//lagomCassandraEnabled in ThisBuild := false

//lagomKafkaEnabled in ThisBuild := false
//lagomKafkaAddress in ThisBuild := "10.65.103.58:9092"