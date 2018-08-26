name := "tagger"

version := "0.1"

scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.15"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5",
  "commons-io" % "commons-io" % "2.6",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)