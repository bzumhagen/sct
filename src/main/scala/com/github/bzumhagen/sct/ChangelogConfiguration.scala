package com.github.bzumhagen.sct

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._
import scala.util.matching.Regex

class ChangelogConfiguration(config: Config = ConfigFactory.load()) {
  val versionPattern  : Regex       = config.getString("sct.patterns.version").r
  val tagPattern      : Regex       = config.getString("sct.patterns.tag").r
  val referencePattern: Regex       = config.getString("sct.patterns.reference").r
  val tags            : Set[String] = config.getStringList("sct.tags").asScala.toSet
}
