package com.evolution.playjson.verify

import com.evolution.playjson.{PlayJsonBinaryCompat, PlayJsonBinaryCompatSuite}

class PlayframeworkSpec extends PlayJsonBinaryCompatSuite {

  test("play-json is actually on the classpath") {
    assert(
      classLoader.getResource("play/api/libs/json/JsValue.class") != null,
      "play-json is missing, the guard would pass for the wrong reason"
    )
  }

  test("no problems are reported") {
    assertEquals(PlayJsonBinaryCompat.check(classLoader), Nil)
  }
}
