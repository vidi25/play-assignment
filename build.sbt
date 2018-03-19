name := """play-assignment"""
organization := "edu.knoldus"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  guice,
  ehcache,
  specs2 % Test,
  evolutions,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "mysql" % "mysql-connector-java" % "5.1.35",
  "com.h2database" % "h2" % "1.4.196"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "edu.knoldus.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "edu.knoldus.binders._"
