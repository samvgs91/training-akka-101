name := "training-akka"

version := "0.1"

scalaVersion := "2.13.11"

val akkaVersion = "2.8.2"

libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
        "org.scalatest" %% "scalatest" % "3.2.9"
      )