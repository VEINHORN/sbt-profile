package com.veinhorn.sbt.plugin.profile

import sbt.Keys._
import sbt._
import sbt.complete.Parsers.spaceDelimited

/**
  * Created by Boris Korogvich on 12.09.2017.
  */
object ProfilePlugin extends AutoPlugin {
  // override def trigger = allRequirements

  type Property = Tuple2[String, String]

  case class Profile(id: String,
                     properties: Seq[Property] = List.empty,
                     default: Boolean = false)

  object autoImport {
    // Settings
    val profiles = settingKey[Seq[Profile]]("Specify profiles")
    // Tasks
    val showProfiles = taskKey[Unit]("Show all profiles")
    val showProfileProperties = inputKey[Unit]("Show profile properties")
  }

  import autoImport._

  private val init = (state: State) => {
    /*println("initialization...")
    println(sourceDirectory)*/
    state
  }

  override lazy val projectSettings = Seq(
    showProfiles := {
      profiles.value.map {
        case Profile(id, _, true)  => s"  -$id *"
        case Profile(id, _, false) => s"  -$id"
      } foreach println
    },
    /** Should be an ability to print properties for multiple profiles */
    showProfileProperties := {
      // val args = spaceDelimited("").parsed
      spaceDelimited("").parsed.foreach { profile =>
        profiles.value.find(_.id == profile) foreach { p =>
          println(p.id)
          p.properties.map { case (key, value) => s"  ")
        }
      }

      // args.foreach(println)
    },
    onLoad in Global ~= (init compose _)
  )

}
