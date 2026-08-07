package com.evolution.playjson.verify

import com.evolution.playjson.PlayJsonBinaryCompat
import munit.FunSuite

class MixedSpec extends FunSuite {

  private def classLoader = getClass.getClassLoader

  private lazy val problems = PlayJsonBinaryCompat.check(classLoader)

  test("play-functional is actually duplicated on the classpath") {
    val locations = classLoader.getResources("play/api/libs/functional/Applicative.class")
    var count = 0
    while (locations.hasMoreElements) {
      locations.nextElement()
      count += 1
    }
    assert(count > 1, s"expected two play-functional jars, found $count")
  }

  test("duplicated play-functional is reported") {
    assert(
      problems.exists { problem =>
        problem.startsWith(PlayJsonBinaryCompat.PlayFunctional) && problem.contains("is present")
      },
      problems
    )
  }

  test("legacy coordinates are reported") {
    assert(problems.exists(_.contains(PlayJsonBinaryCompat.Legacy)), problems)
  }
}
