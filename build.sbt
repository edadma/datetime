ThisBuild / versionScheme := Some("semver-spec")

lazy val datetime = crossProject(JSPlatform, JVMPlatform, NativePlatform).in(file(".")).
  settings(
    name := "datetime",
    version := "0.1.10",
    scalaVersion := "2.13.6",
    scalacOptions ++=
      Seq(
        "-deprecation", "-feature", "-unchecked",
        "-language:postfixOps", "-language:implicitConversions", "-language:existentials", "-language:dynamics",
        "-Xasync"
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
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.9" % "test",
    libraryDependencies += "io.github.edadma" %%% "char-reader" % "0.1.6",
    publishMavenStyle := true,
    Test / publishArtifact := false,
    licenses += "ISC" -> url("https://opensource.org/licenses/ISC")
  ).
  jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.0.0" % "provided",
  ).
  nativeSettings(
    nativeLinkStubs := true
  ).
  jsSettings(
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
//    Test / scalaJSUseMainModuleInitializer := true,
//    Test / scalaJSUseTestModuleInitializer := false,
    Test / scalaJSUseMainModuleInitializer := false,
    Test / scalaJSUseTestModuleInitializer := true,
    scalaJSUseMainModuleInitializer := true,
  )
