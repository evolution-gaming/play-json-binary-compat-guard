package com.evolution.playjson

import munit.FunSuite

/** Extend in your project to fail the build on a broken `play-json` classpath. */
abstract class PlayJsonBinaryCompatSuite extends FunSuite {

  protected def classLoader: ClassLoader = getClass.getClassLoader

  test("play-json on the classpath is binary compatible") {
    val problems = PlayJsonBinaryCompat.check(classLoader)
    assert(problems.isEmpty, problems.mkString("\n", "\n", "\n"))
  }
}
