package com.evolution.playjson

import munit.FunSuite

class PlayJsonBinaryCompatTest extends FunSuite {

  import PlayJsonBinaryCompat._

  private val playframework = Map(
    PlayJson -> List(
      "file:/cache/https/repo1.maven.org/maven2/org/playframework/play-json_2.13/3.0.6/play-json_2.13-3.0.6.jar"
    ),
    PlayFunctional -> List(
      "file:/cache/https/repo1.maven.org/maven2/org/playframework/play-functional_2.13/3.0.6/play-functional_2.13-3.0.6.jar"
    )
  )

  private val typesafe = Map(
    PlayJson -> List(
      "file:/cache/https/repo1.maven.org/maven2/com/typesafe/play/play-json_2.13/2.10.8/play-json_2.13-2.10.8.jar"
    ),
    PlayFunctional -> List(
      "file:/cache/https/repo1.maven.org/maven2/com/typesafe/play/play-functional_2.13/2.10.8/play-functional_2.13-2.10.8.jar"
    )
  )

  private def locate(classpath: Map[String, List[String]]): String => List[String] =
    className => classpath.getOrElse(className, Nil)

  test("empty classpath is fine") {
    assertEquals(provenance(locate(Map.empty)), Nil)
  }

  test("org.playframework is fine") {
    assertEquals(provenance(locate(playframework)), Nil)
  }

  test("com.typesafe.play is reported") {
    val problems = provenance(locate(typesafe))
    assertEquals(problems.size, 2)
    assert(problems.forall(_.contains(Legacy)), problems)
  }

  test("mixed coordinates are reported") {
    val problems = provenance(locate(playframework ++ typesafe.view.filterKeys(_ == PlayFunctional).toMap))
    assertEquals(problems.size, 1)
    assert(problems.head.contains(Legacy), problems)
  }

  test("duplicates are reported") {
    val problems = provenance(locate(playframework.updated(PlayJson, playframework(PlayJson) :+ "file:/lib/fat.jar")))
    assertEquals(problems.size, 1)
    assert(problems.head.contains("is present 2 times"), problems)
  }

  test("play-json without play-functional is reported") {
    val problems = provenance(locate(playframework - PlayFunctional))
    assertEquals(problems.size, 1)
    assert(problems.head.contains("is not"), problems)
  }

  test("group is derived from maven and ivy layouts") {
    assertEquals(groupOf("file:/cache/maven2/org/playframework/play-json_3/3.0.6/play-json_3-3.0.6.jar"), Some(Current))
    assertEquals(
      groupOf("file:/.ivy2/cache/com.typesafe.play/play-json_2.13/jars/play-json_2.13-2.10.8.jar"),
      Some(Legacy)
    )
    assertEquals(groupOf("file:/lib/fat.jar"), None)
  }

  test("group is derived from a flattened lib layout") {
    assertEquals(groupOf("file:/opt/docker/lib/com.typesafe.play.play-json_2.13-2.10.8.jar"), Some(Legacy))
    assertEquals(groupOf("file:/opt/docker/lib/org.playframework.play-json_2.13-3.0.6.jar"), Some(Current))
  }

  test("flattened legacy coordinates are reported") {
    val flattened = Map(
      PlayJson -> List("file:/opt/docker/lib/com.typesafe.play.play-json_2.13-2.10.8.jar"),
      PlayFunctional -> List("file:/opt/docker/lib/com.typesafe.play.play-functional_2.13-2.10.8.jar")
    )
    val problems = provenance(locate(flattened))
    assertEquals(problems.size, 2)
    assert(problems.forall(_.contains(Legacy)), problems)
  }

  test("location is derived from a resource url") {
    val resource = Classpath.resourceOf(PlayJson)
    assertEquals(Classpath.locationOf(s"jar:file:/lib/play-json.jar!/$resource", resource), "file:/lib/play-json.jar")
    assertEquals(Classpath.locationOf(s"file:/target/classes/$resource", resource), "file:/target/classes/")
  }

  test("nested jars are distinguished") {
    val resource = Classpath.resourceOf(PlayJson)
    def nested(version: String) =
      Classpath.locationOf(s"jar:file:/app.jar!/BOOT-INF/lib/play-json_2.13-$version.jar!/$resource", resource)
    assertEquals(nested("2.10.8"), "file:/app.jar!/BOOT-INF/lib/play-json_2.13-2.10.8.jar")
    assertNotEquals(nested("2.10.8"), nested("3.0.6"))
  }
}
