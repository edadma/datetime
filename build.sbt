import org.scalajs.jsenv.nodejs.NodeJSEnv

ThisBuild / licenses += "ISC" -> url("https://opensource.org/licenses/ISC")
ThisBuild / versionScheme := Some("semver-spec")

publish / skip := true

lazy val datetime = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("."))
  .settings(
    name := "datetime",
    version := "0.1.19",
    scalaVersion := "3.5.2",
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
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.19" % "test",
    libraryDependencies += "io.github.edadma" %%% "char-reader" % "0.1.15",
    publishMavenStyle := true,
    Test / publishArtifact := false,
    licenses += "ISC" -> url("https://opensource.org/licenses/ISC"),
  )
  .jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
  )
  .nativeSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
  )
  .jsSettings(
    jsEnv := new NodeJSEnv(NodeJSEnv.Config().withExecutable("/home/ed/.nvm/versions/node/v18.20.4/bin/node")),
//    Test / scalaJSUseMainModuleInitializer := true,
//    Test / scalaJSUseTestModuleInitializer := false,
    Test / scalaJSUseMainModuleInitializer := false,
    Test / scalaJSUseTestModuleInitializer := true,
    scalaJSUseMainModuleInitializer := false,
  )
