package com.github.bzumhagen.sct

import java.util.regex.Pattern

import org.scalatest.{FlatSpec, Matchers}

class ChangelogConfigurationTest extends FlatSpec with Matchers {

  "ChangelogConfiguration" should "load the default configuration" in {
    val config = ChangelogConfiguration.load()
    val expectedName = "SCT"
    val expectedVersionPattern = "[\\s\\S]*\nversion: (.+)[\\s\\S]*".r
    val expectedTagPattern = "[\\s\\S]*\ntag: (.+)[\\s\\S]*".r
    val expectedReferencePattern = "[\\s\\S]*\nresolves: (.+)[\\s\\S]*".r
    val expectedTags = Set(
      "Added",
      "Changed",
      "Removed",
      "Deprecated"
    )

    config.name shouldBe expectedName
    config.versionPattern.regex shouldBe expectedVersionPattern.regex
    config.tagPattern.regex shouldBe expectedTagPattern.regex
    config.referencePattern.regex shouldBe expectedReferencePattern.regex
    config.tags shouldBe expectedTags
    config.smartGrouping shouldBe true
  }
}
