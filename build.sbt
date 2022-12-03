ThisBuild / licenses += "ISC" -> url("https://opensource.org/licenses/ISC")
ThisBuild / versionScheme := Some("semver-spec")

publish / skip := true

lazy val datetime = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("."))
  .settings(
    name := "datetime",
    version := "0.1.18",
    scalaVersion := "3.2.1",
    scalacOptions ++=
      Seq(
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:postfixOps",
        "-language:implicitConversions",
        "-language:existentials",
        "-language:dynamics",
      ),
    organization := "io.github.edadma",
//    publishTo := Some(
//      "Artifactory Realm" at "https://hyperreal.jfrog.io/artifactory/default-maven-virtual"
//    ),
//      credentials += Credentials(
//      "Artifactory Realm",
//      "hyperreal.jfrog.io",
//      "edadma@gmail.com",
//      "fW6N-hDhW*XPXhMt"
//    ),
    githubOwner := "edadma",
    githubRepository := "datetime",
    mainClass := Some("Main"),
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.13" % "test",
    libraryDependencies += "io.github.edadma" %%% "char-reader" % "0.1.11",
    publishMavenStyle := true,
    Test / publishArtifact := false,
    licenses += "ISC" -> url("https://opensource.org/licenses/ISC"),
  )
  .jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
  )
  .nativeSettings(
    nativeLinkStubs := true,
  )
  .jsSettings(
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
//    Test / scalaJSUseMainModuleInitializer := true,
//    Test / scalaJSUseTestModuleInitializer := false,
    Test / scalaJSUseMainModuleInitializer := false,
    Test / scalaJSUseTestModuleInitializer := true,
    scalaJSUseMainModuleInitializer := false,
  )
