package com.evolution.playjson.verify

import com.evolution.playjson.PlayJsonBinaryCompat
import munit.FunSuite

class TypesafeSpec extends FunSuite {

  private def classLoader = getClass.getClassLoader

  test("legacy com.typesafe.play play-json is reported") {
    val problems = PlayJsonBinaryCompat.check(classLoader)
    assert(problems.nonEmpty, "legacy play-json was not detected")
    assert(problems.forall(_.contains(PlayJsonBinaryCompat.Legacy)), problems)
  }
}
