package com.evolution.playjson

/** Runtime guard against a broken `play-json` on the classpath.
  *
  * Requires no compile time dependency on `play-json`: everything is decided by inspecting the classpath of a class
  * loader. When `play-json` is absent there is nothing to guard and the check succeeds.
  */
object PlayJsonBinaryCompat {

  val PlayJson = "play.api.libs.json.JsValue"
  val PlayFunctional = "play.api.libs.functional.Applicative"

  val Legacy = "com.typesafe.play"
  val Current = "org.playframework"

  private val Modules = List(
    "play.api.libs.json.Json$",
    "play.api.libs.json.Reads$",
    "play.api.libs.json.OWrites$"
  )

  /** Problems found on the classpath, empty when everything is fine. */
  def check(loader: ClassLoader): List[String] =
    provenance(Classpath.locations(_, loader)) ++ linkage(loader)

  private[playjson] def provenance(locate: String => List[String]): List[String] = {
    val json = locate(PlayJson)
    val functional = locate(PlayFunctional)
    if (json.isEmpty && functional.isEmpty) Nil
    else {
      val duplicated = List(PlayJson -> json, PlayFunctional -> functional).collect {
        case (className, locations) if locations.sizeIs > 1 =>
          s"$className is present ${locations.size} times: ${locations.mkString(", ")}"
      }
      val legacy = (json ++ functional).distinct.filter { location => groupOf(location).contains(Legacy) }.map {
        location => s"comes from the legacy $Legacy coordinates, expected $Current: $location"
      }
      val incomplete =
        if (json.nonEmpty && functional.isEmpty) List(s"$PlayJson is on the classpath, but $PlayFunctional is not")
        else Nil
      duplicated ++ legacy ++ incomplete
    }
  }

  private[playjson] def groupOf(location: String): Option[String] = {
    val path = location.replace('\\', '/')
    def contains(group: String) =
      path.contains(s"/${group.replace('.', '/')}/") || path.contains(s"/$group/")
    if (contains(Legacy)) Some(Legacy)
    else if (contains(Current)) Some(Current)
    else None
  }

  private def linkage(loader: ClassLoader): List[String] =
    if (!Classpath.exists(PlayJson, loader)) Nil
    else
      Modules.flatMap { className =>
        try {
          Class.forName(className, true, loader)
          None
        } catch {
          case _: ClassNotFoundException => None
          case e: LinkageError           => Some(s"$className failed to initialise: $e")
        }
      }
}
