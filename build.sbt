organization := "com.agilogy"

name := "option-change"

version := "1.0"

scalaVersion := "2.10.6"

javaVersion := "1.8"

crossScalaVersions := Seq("2.10.6","2.11.7")

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)

resolvers ++= Seq("Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.6",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

publishMavenStyle := false

// --> bintray

bintrayRepository := "scala"

bintrayOrganization := Some("agilogy")

// packageLabels in bintray := Seq("scala")

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// <-- bintray