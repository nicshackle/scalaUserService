name := "scalaUsers"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "joda-time" % "joda-time" % "2.10.10",
  "com.typesafe.akka" %% "akka-stream" % "2.6.8",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.1",
  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.8",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.1"
)