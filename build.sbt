organization in ThisBuild := "org.shinhwagk"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `oso` = (project in file("."))
  .aggregate(`oso-query-api`, `oso-query-impl`)

lazy val `oso-query-api` = (project in file("oso-query-api"))
  .settings(
    resolvers ++= Seq("Spray Repository" at "http://dev.rtmsoft.me/nexus/content/groups/public/"),
    libraryDependencies ++= Seq(
      "com.wingtech" % "ojdbc" % "7",
      lagomScaladslApi
    )
  )

lazy val `oso-query-impl` = (project in file("oso-query-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`oso-query-api`)

lagomCassandraCleanOnStart in ThisBuild := false
lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false