import Dependencies._

ThisBuild / scalaVersion     := "0.24.0-RC1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ch.gennri"
ThisBuild / organizationName := "Saskia"
ThisBuild / libraryDependencies ++= Seq(
    // scalaTest % Test
)
lazy val root = (project in file("."))
