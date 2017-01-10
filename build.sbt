organization in ThisBuild := "org.shinhwagk"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `oso` = (project in file("."))
  .aggregate(`oso-config-api`, `oso-config-impl`)

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

//lazy val `os-monitor-api` = (project in file("oso-monitor-api"))
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
//      lagomScaladslTestKit,
//      macwire,
//      scalaTest
//    )
//  )
//  .settings(lagomForkedTestSettings: _*)
//  .dependsOn(`oso-query-api`)

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

lagomCassandraCleanOnStart in ThisBuild := false
lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false