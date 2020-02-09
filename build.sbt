import Dependencies._

ThisBuild / scalaVersion     := "0.22.0-RC1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ch.gennri"
ThisBuild / organizationName := "Saskia"
ThisBuild / libraryDependencies ++= Seq(
//    "org.typelevel" %% "cats-core" % "2.0.0",
    // scalaTest % Test
)
// lazy val root = (project in file("."))
//   .settings(
//     name := "Functional Scala",
//     scalaVersion := "0.22.0-RC1"
// )
lazy val root = project

// lazy val `chapter-02` = project
// lazy val `chapter-03` = project
// lazy val `chapter-04` = project
// lazy val `chapter-05` = project
// // lazy val `chapter-05` = project
// lazy val `chapter-09` = project