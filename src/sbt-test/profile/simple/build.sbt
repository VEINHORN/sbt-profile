import com.veinhorn.sbt.plugin.profile.ProfilePlugin.Profile

lazy val root = (project in file("."))
  .enablePlugins(com.veinhorn.sbt.plugin.profile.ProfilePlugin)
  .settings(
    name := "profile-example",
    version := "0.1",
    scalaVersion := "2.12.3",
    profiles := List(
      Profile(
        id = "dev",
        properties = List(
          "db.username" -> "DevUser",
          "db.password" -> "DevPass123"
        ),
        default = true,
        resourceDirs = (baseDirectory.value / "src/main/profiles/dev/res") :: Nil,
        sourceDirs = (baseDirectory.value / "src/main/profiles/dev/src") :: Nil
      ),
      Profile(
        id = "staging",
        properties = List(
          "db.username" -> "StagingUser",
          "db.password" -> "StagingPass456"
        ),
        resourceDirs = (baseDirectory.value / "src/main/profiles/staging/res") :: Nil
      ),
      Profile(
        id = "prod",
        properties = List(
          "db.username" -> "ProdUser",
          "db.password" -> "ProdPass789"
        ),
        resourceDirs = (baseDirectory.value / "src/main/profiles/prod/res") :: Nil,
        sourceDirs = (baseDirectory.value / "src/main/profiles/prod/src") :: Nil
      )
    )
  )
