package com.veinhorn.sbt.plugin.profile

import java.io.PrintWriter

import org.scalatest.FlatSpec

import scala.io.Source

/**
  * Created by Boris Korogvich on 21.09.2017.
  */
class ProfilePluginSpec extends FlatSpec {
  it should "replace properties in file" in {
    val res = Source.fromFile("input.properties").mkString
    val replaced = res.replaceAll("\\$\\{db.username\\}", "custom name")
    new PrintWriter("input.properties") { write(replaced); close() }
    val test = "test"
  }
}
