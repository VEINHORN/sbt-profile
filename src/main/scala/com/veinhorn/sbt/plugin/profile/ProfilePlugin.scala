package com.veinhorn.sbt.plugin.profile

import sbt.Keys._
import sbt._

/**
  * Created by Boris Korogvich on 12.09.2017.
  */
object ProfilePlugin extends AutoPlugin {
  override lazy val projectSettings = Seq(commands += helloCommand)
  lazy val helloCommand =
    Command.command("hello") { (state: State) =>
      println("Hi!")
      state
    }
}
