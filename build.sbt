organization in ThisBuild := "org.wex"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"


lazy val root = (project in file("."))
  .aggregate(api, impl)

lazy val api = (project in file("api"))
  .settings(libraryDependencies += lagomScaladslApi)

lazy val impl = (project in file("impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided",
      "org.scalatest" %% "scalatest" % "3.0.1" % Test,
      "mysql" % "mysql-connector-java" % "6.0.5",
      "com.typesafe.slick" %% "slick" % "3.1.1",
      lagomScaladslTestKit
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(api)

lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false

fork in run := true