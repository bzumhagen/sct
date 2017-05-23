package com.github.bzumhagen.sct

import java.util.regex.Pattern

import org.scalatest.{FlatSpec, Matchers}

class ChangelogConfigurationTest extends FlatSpec with Matchers {

  "ChangelogConfiguration" should "load the default configuration" in {
    val config = new ChangelogConfiguration
    val expectedVersionPattern = "[\\s\\S]*\nversion: (.+)[\\s\\S]*".r
    val expectedTagPattern = "[\\s\\S]*\ntag: (.+)[\\s\\S]*".r
    val expectedReferencePattern = "[\\s\\S]*\nresolves: (.+)[\\s\\S]*".r
    val expectedTags = Set(
      "added",
      "changed",
      "removed",
      "deprecated"
    )

    config.versionPattern.regex shouldBe expectedVersionPattern.regex
    config.tagPattern.regex shouldBe expectedTagPattern.regex
    config.referencePattern.regex shouldBe expectedReferencePattern.regex
    config.tags shouldBe expectedTags
  }
}
