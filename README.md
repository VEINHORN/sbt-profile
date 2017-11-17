# sbt-profile

[![Travis](https://travis-ci.org/VEINHORN/sbt-profile.svg?branch=master)]()
[![Gitter](https://img.shields.io/gitter/room/nwjs/nw.js.svg)](https://gitter.im/sbt-profile-profile/Lobby?utm_source=share-link&utm_medium=link&utm_campaign=share-link)

The `sbt-profile` plugin allows you to customize your build process depending on selected environment in Maven-like style. You can specify different configuration files and properties for your `dev`, `test`, `staging`, `prod` environment or create your own profile structure.

What does `sbt-profile` can do:
* Replace properties in your resources/sources based on profile
* Specify different resources/sources folders based on profile
* Override settings based on profile (not implemented yet)

## Installation

## Usage
Just specify your profiles and mark default profile which be used by default.

```scala
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
        id = "staging",
        properties = List(
          "db.username" -> "StagingUser",
          "db.password" -> "StagingPass456"
        ),
        resourceDirs = (baseDirectory.value / "src/main/profiles/staging") :: Nil
      ),
      Profile(
        id = "prod",
        properties = List(
          "db.username" -> "ProdUser",
          "db.password" -> "ProdPass789"
        ),
        resourceDirs = (baseDirectory.value / "src/main/profiles/prod") :: Nil
      )
    )
  )

```

## License

    Copyright 2017, 2017 Boris Korogvich

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
