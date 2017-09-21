package com.veinhorn.sbt.plugin.profile

import java.io.PrintWriter

import sbt.Keys._
import sbt._
import sbt.complete.Parsers.spaceDelimited

import scala.io.Source

/**
  * Created by Boris Korogvich on 12.09.2017.
  */
object ProfilePlugin extends AutoPlugin {
  type Property = Tuple2[String, String]

  case class Profile(id: String,
                     properties: Seq[Property] = List.empty,
                     default: Boolean = false)

  object autoImport {
    // Settings
    val profiles = settingKey[Seq[Profile]]("Specify profiles")
    // Tasks
    lazy val showProfiles = taskKey[Unit]("Show all profiles")
    lazy val showProfileProperties = inputKey[Unit]("Show profile properties")
    lazy val applyProfile = taskKey[Unit]("Apply plugin settings")

    lazy val selectProfile = inputKey[Unit]("Select profile")
  }

  import autoImport._

  /*private val init = (state: State) => {
    println("initializing...")
    state
  }*/

  override lazy val projectSettings = Seq(
    showProfiles := {
      profiles.value.map {
        case Profile(id, _, true)  => s"  -$id *"
        case Profile(id, _, false) => s"  -$id"
      } foreach println
    },
    /** Should be an ability to print properties for multiple profiles */
    showProfileProperties := {
      val args = spaceDelimited("").parsed

      args.foreach(println)
    },
    /** Apply default profile or passed as parameter */
    applyProfile := {
      def replaceInFile(resFile: File) = {
        println(s"Replacing ${resFile.toPath.toString} ...")

        val res = Source.fromFile(resFile).mkString
        var replaced = res

        profiles.value.find(_.default).foreach { profile =>
          println(s"Selected *${profile.id}* profile")
          profile.properties.foreach { case (key, value) =>
              val regex = "\\$\\{" + key + "\\}"

              replaced = replaced.replaceAll(regex, value)
              if (resFile.getName == "creds.properties") {
                println("regex=" + regex)
                println("resource=" + res)
                println("replaced=" + replaced)
              }
          }
        }
        new PrintWriter(resFile) { write(replaced); close() }
      }

      (copyResources in Compile).value

      val resourceFiles = (resources in Compile).value
      val targetDir = (classDirectory in Compile).value

      /*println("=== Resources")
      resourceFiles.filter(_.isFile).foreach(f => println(f.toPath.toString))

      println("=== Resources in target")
      Option(targetDir.listFiles()).map(_.filter(_.isFile)).foreach(_.foreach(f => println(f.toPath.toString)))*/

      Option(targetDir.listFiles())
        .map(_.filter(_.isFile))
        .foreach(_.foreach(replaceInFile))
    },
    selectProfile := {
      val args = spaceDelimited("").parsed

      if (args.size == 1) {
        profiles ~= { profile =>
          val found = profile.find(_.id == args(0))
          if (found.isDefined) profile.map(_.copy(default = false))
              .map { p =>
                if (p.id == args.head) p.copy(default = true)
                p
              }
          else profile
          // profile
          //profile map {
          //  case p@Profile(id, _, _) if id == args(0) => p.copy()
          // }
        }
      } else {
        println("Cannot select this profile")
      }
      }
    //,onLoad in Global ~= (init compose _) // some init actions
  )
}
