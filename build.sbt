lazy val root = (project in file("."))
  .settings(
    name := "sbt-profile",
    version := "0.1-SNAPSHOT",
    organization := "com.veinhorn.sbt.plugin",
    scalaVersion := "2.12.3",
    sbtPlugin := true
  )

// ScriptedPlugin.scriptedSettings
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false
