ThisBuild / organization := "com.evolution"
ThisBuild / scalaVersion := "2.13.18"
ThisBuild / crossScalaVersions := Seq("2.13.18", "3.3.8")
ThisBuild / homepage := Some(uri("https://github.com/evolution-gaming/play-json-binary-compat-guard"))
ThisBuild / licenses := Seq("MIT" -> uri("https://opensource.org/licenses/MIT"))
ThisBuild / organizationName := "Evolution"
ThisBuild / organizationHomepage := Some(uri("https://evolution.com"))
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / publishTo := Some(Resolver.evolutionReleases)
ThisBuild / credentials ++= sys.env.get("SBT_CREDENTIALS").map { path => Credentials(new java.io.File(path)) }

val munit = "org.scalameta" %% "munit" % "1.3.4"

addCommandAlias("check", "scalafmtSbtCheck; scalafmtCheckAll")

lazy val root = project
  .in(file("."))
  .aggregate(verifyPlayframework, verifyTypesafe)
  .settings(
    name := "play-json-binary-compat-guard",
    libraryDependencies += munit,
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val verifyPlayframework = project
  .in(file("verify/playframework"))
  .dependsOn(LocalProject("root") % "test->compile")
  .settings(verifySettings)
  .settings(
    name := "verify-playframework",
    libraryDependencies += "org.playframework" %% "play-json" % "3.0.6" % Test
  )

lazy val verifyTypesafe = project
  .in(file("verify/typesafe"))
  .dependsOn(LocalProject("root") % "test->compile")
  .settings(verifySettings)
  .settings(
    name := "verify-typesafe",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.8" % Test
  )

lazy val verifySettings: Seq[Setting[?]] = Seq(
  publish / skip := true,
  libraryDependencies += munit % Test,
  testFrameworks += new TestFramework("munit.Framework")
)
