organization := "com.agilogy"

name := "option-change"

version := "0.1"

scalaVersion := "2.10.4"

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)

resolvers ++= Seq("Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.9",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

publishMavenStyle := false

// --> bintray

// repository in bintray := "scala"

bintrayOrganization in bintray := Some("agilogy")

// packageLabels in bintray := Seq("scala")

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// <-- bintray