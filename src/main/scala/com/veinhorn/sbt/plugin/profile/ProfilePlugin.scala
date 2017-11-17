package com.veinhorn.sbt.plugin.profile

import java.io.PrintWriter

import sbt.Keys._
import sbt._

import scala.io.Source

/**
  * Created by Boris Korogvich on 12.09.2017.
  */
object ProfilePlugin extends AutoPlugin {
  type Property = (String, String)

  case class Profile(id: String,
                     properties: Seq[Property] = List.empty,
                     default: Boolean = false,
                     resourceDirs: Seq[File] = List.empty,
                     sourceDirs: Seq[File] = List.empty)

  object autoImport {
    val profiles: SettingKey[Seq[Profile]] = settingKey[Seq[Profile]]("All specified profiles")

    lazy val showProfiles: TaskKey[Unit] = taskKey[Unit]("Show all available profiles")
  }

  import autoImport._

  /** Selects provided profile */
  def selectProfile: Command = Command.single("selectProfile") { (state, profile) =>
    val log = Project.extract(state).get(sLog)
    val profiles = Project.extract(state).get(autoImport.profiles)

    profiles.find(_.id == profile) match {
      case Some(p) =>
        log.info(s"Selected $profile profile")

        val extracted = Project.extract(state)
        // Select new default profile
        val modifiedProfiles = profiles.map {
          case p@Profile(id, _, _, _, _) if id == profile => p.copy(default = true)
          case p@Profile(_, _, default, _, _)             => if (!default) p else p.copy(default = false)
        }
        extracted.append(Seq(
          autoImport.profiles := modifiedProfiles,
          (unmanagedResourceDirectories in Compile) ~= { resources =>
            val newResources = resources.filterNot(res => profiles.find(_.default).exists(_.resourceDirs.contains(res)))
              .filterNot(res => modifiedProfiles.find(_.default).exists(_.resourceDirs.contains(res)))
            newResources ++ modifiedProfiles.find(_.default).map(_.resourceDirs).getOrElse(List.empty)
          },

          (unmanagedSourceDirectories in Compile) ~= { sources => // need to remove source directories from previous profile
            // Filter source directories with possible duplicates and from previous selected profile
            val newSources = sources.filterNot(src => profiles.find(_.default).exists(_.sourceDirs.contains(src)))
              .filterNot(src => modifiedProfiles.find(_.default).exists(_.sourceDirs.contains(src)))
            newSources ++ modifiedProfiles.find(_.default).map(_.sourceDirs).getOrElse(List.empty)
          }
        ), state)
      case None   =>
        log.info(s"Profile with name $profile does not exist")
        state
    }
  }

  lazy val baseProfileSettings: Seq[Def.Setting[_]] = Seq(
    /** By default add resource directories from default profile */
    unmanagedResourceDirectories ++= {
      profiles.value.find(_.default).map(_.resourceDirs) getOrElse List.empty
    },

    unmanagedSourceDirectories ++= {
      profiles.value.find(_.default).map(_.sourceDirs) getOrElse List.empty
    },

    /** Replace properties in copied resources with properties from default profile */
    copyResources := {
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

      val copied = copyResources.value

      val targetDir = classDirectory.value

      Option(targetDir.listFiles())
        .map(_.filter(_.isFile))
        .foreach(_.foreach(replaceInFile))

      copied
    },

    showProfiles := {
      println("Profiles:")
      profiles.value.map {
        case Profile(id, _, true, _, _)  => s"\t-$id *"
        case Profile(id, _, false, _, _) => s"\t-$id"
      } foreach println
    }
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    Seq(commands += selectProfile) ++ inConfig(Compile)(baseProfileSettings)
}
