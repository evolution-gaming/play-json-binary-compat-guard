package com.evolution.playjson

class PlayJsonBinaryCompatSpec extends PlayJsonBinaryCompatSuite {

  test("play-json is not a dependency of the guard itself") {
    assertEquals(Classpath.locations(PlayJsonBinaryCompat.PlayJson, classLoader), Nil)
    assertEquals(Classpath.locations(PlayJsonBinaryCompat.PlayFunctional, classLoader), Nil)
  }
}
