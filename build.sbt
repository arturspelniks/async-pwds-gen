name := "async-pwds-gen"

version := "0.1"

scalaVersion := "2.12.8"

val akkaVersion = "2.5.13"

libraryDependencies += "junit" % "junit" % "4.10" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.19"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5"
)