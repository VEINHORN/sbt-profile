import com.veinhorn.sbt.plugin.profile.ProfilePlugin.Profile

lazy val root = (project in file("."))
  .settings(
    name := "profile-example",
    version := "0.1",
    scalaVersion := "2.12.3",
    profiles := List(
      Profile(
        id = "dev",
        properties = List(
          "db.password" -> "dev password"
        ),
        default = true
      ),
      Profile(
        id = "prod",
        properties = List(
          "db.password" -> "prod password"
        )
      )
    )
  )
  .enablePlugins(com.veinhorn.sbt.plugin.profile.ProfilePlugin)