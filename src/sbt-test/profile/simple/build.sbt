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
        resourceDirs = (baseDirectory.value / "src/main/profiles/dev") :: Nil
      ),
      Profile(
        id = "prod",
        properties = List(
          "db.username" -> "ProdUser",
          "db.password" -> "ProdPass456"
        )
      )
    )
  )
