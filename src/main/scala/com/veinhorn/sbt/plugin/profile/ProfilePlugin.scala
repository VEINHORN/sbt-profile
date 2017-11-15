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
  type Property = (String, String)

  case class Profile(id: String,
                     properties: Seq[Property] = List.empty,
                     default: Boolean = false,
                     resourceDirs: Seq[File] = List.empty)

  object autoImport {
    // Settings
    val profiles: SettingKey[Seq[Profile]] = settingKey[Seq[Profile]]("Specify profiles")
    // Tasks
    lazy val showProfiles: TaskKey[Unit] = taskKey[Unit]("Show all profiles")
  }

  import autoImport._

  lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
    /** By default add resource directories from default profile */
    (unmanagedResourceDirectories in Compile) ++= {
      profiles.value.find(_.default).map(_.resourceDirs) getOrElse List.empty
    },

    /** Replace properties in copied resources with properties from default profile */
    (copyResources in Compile) := {
      def replaceInFile(resFile: File) = {
        println(s"Replacing ${resFile.toPath.toString} ...")

        val res = Source.fromFile(resFile).mkString
        var replaced = res

        profiles.value.find(_.default).foreach { profile =>
          profile.properties.foreach { case (key, value) =>
            replaced = replaced.replaceAll("\\$\\{" + key + "\\}", value)
          }
        }
        new PrintWriter(resFile) { write(replaced); close() }
      }

      val copied = (copyResources in Compile).value

      val targetDir = (classDirectory in Compile).value

      Option(targetDir.listFiles())
        .map(_.filter(_.isFile))
        .foreach(_.foreach(replaceInFile))

      copied
    },

    (showProfiles in Compile) := {
      println("Profiles:")
      profiles.value.map {
        case Profile(id, _, true, _)  => s"\t-$id *"
        case Profile(id, _, false, _) => s"\t-$id"
      } foreach println
    },

    commands ++= Seq(selectProfile)
  )

  /** Selects provided profile */
  def selectProfile: Command = Command.single("selectProfile") { (state, profile) =>
    val log = Project.extract(state).get(sLog)
    val profiles = Project.extract(state).get(autoImport.profiles)

    profiles.find(_.id == profile) match {
      case Some(p) =>
        log.info(s"Selected $profile profile")

        val extracted = Project.extract(state)
        val modifiedProfiles = profiles.map {
          case p@Profile(id, _, _, _) if id == profile => p.copy(default = true)
          case p@Profile(_, _, default, _)             => if (!default) p else p.copy(default = false)
        }
        extracted.append(Seq(
          autoImport.profiles := modifiedProfiles,
          (unmanagedResourceDirectories in Compile) ~= { p =>
            p.head +: modifiedProfiles.find(_.default).map(_.resourceDirs).getOrElse(List.empty)
          }
        ), state)
      case None   =>
        log.info(s"Profile with name $profile does not exist")
        state
    }
  }

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseSettings

}
