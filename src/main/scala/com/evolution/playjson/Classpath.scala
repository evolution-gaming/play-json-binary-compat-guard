package com.evolution.playjson

import scala.jdk.CollectionConverters._

private[playjson] object Classpath {

  def locations(className: String, loader: ClassLoader): List[String] = {
    val resource = resourceOf(className)
    loader
      .getResources(resource)
      .asScala
      .toList
      .map { url => locationOf(url.toString, resource) }
      .distinct
  }

  def exists(className: String, loader: ClassLoader): Boolean =
    loader.getResource(resourceOf(className)) != null

  def resourceOf(className: String): String =
    className.replace('.', '/') + ".class"

  def locationOf(url: String, resource: String): String = {
    val path = url.stripPrefix("jar:")
    path.indexOf("!/") match {
      case -1 => path.stripSuffix(resource)
      case i  => path.substring(0, i)
    }
  }
}
