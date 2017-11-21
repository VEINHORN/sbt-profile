import sbt.ScriptedPlugin.autoImport.scriptedBufferLog

lazy val root = (project in file("."))
  .settings(
    name := "sbt-profile",
    version := "0.1.2",
    organization := "com.veinhorn.sbt.plugin",
    scalaVersion := "2.12.3",
    sbtPlugin := true,
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.1",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    ),
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,

    bintrayRepository := "maven",
    bintrayPackage := "sbt-profile",
    publishMavenStyle := false
  )
