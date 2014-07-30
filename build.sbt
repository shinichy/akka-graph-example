name := "akka-graph"

version := "1.0.0"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-kernel" % "2.3.4",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test"
)

