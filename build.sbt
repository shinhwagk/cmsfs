organization in ThisBuild := "org.wex"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "3.3"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val query = (project in file("."))
  .aggregate(
    `api`, `impl`
  )

lazy val `api` = (project in file("api"))
  .settings(
    resolvers ++= Seq("Spray Repository" at "http://dev.rtmsoft.me/nexus/content/groups/public/"),
    libraryDependencies ++= Seq(
      "com.wingtech" % "ojdbc" % "7",
      "com.jcraft" % "jsch" % "0.1.54",
      lagomScaladslApi
    )
  )

lazy val `impl` = (project in file("impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`api`)

lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false

fork in run := true