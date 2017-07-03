package com.github.bzumhagen.sct

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._
import scala.util.matching.Regex

object ChangelogConfiguration {
  def load(config: Config = ConfigFactory.load()): ChangelogConfiguration = {
    new ChangelogConfiguration(
      name = config.getString("sct.name"),
      versionPattern = config.getString("sct.patterns.version").r,
      tagPattern = config.getString("sct.patterns.tag").r,
      referencePattern = config.getString("sct.patterns.reference").r,
      tags = config.getStringList("sct.tags").asScala.toSet,
      smartGrouping = config.getBoolean("sct.smartGrouping"),
      showReference = config.getBoolean("sct.showReference")
    )
  }
}

case class ChangelogConfiguration(
  name: String,
  versionPattern: Regex,
  tagPattern: Regex,
  referencePattern: Regex,
  tags: Set[String],
  smartGrouping: Boolean,
  showReference: Boolean
)
