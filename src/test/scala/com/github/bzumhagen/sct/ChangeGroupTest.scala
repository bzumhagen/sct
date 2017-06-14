package com.github.bzumhagen.sct

import java.time.LocalDateTime

import com.github.zafarkhaja.semver.Version
import org.scalatest.{FlatSpec, Matchers}

class ChangeGroupTest extends FlatSpec with Matchers {
  private val Today = LocalDateTime.now().toLocalDate
  private val Tomorrow = Today.plusDays(1)

  "ChangeGroup" should "load changes correctly" in {
    val addedChanges = Seq(
      ChangelogChange("Create project", Version.valueOf("1.0.0"), "Added", Some("XYZ-123"), Today),
      ChangelogChange("Add some functionality", Version.valueOf("1.1.0"), "Added", Some("XYZ-124"), Today)
    )
    val changedChanges = Seq(
      ChangelogChange("Change some behavior", Version.valueOf("1.1.1"), "Changed", Some("XYZ-127"), Today),
      ChangelogChange("Change some behavior again", Version.valueOf("1.1.2"), "Changed", Some("XYZ-128"), Tomorrow)
    )
    val expectedVersion = Version.valueOf("1.1.2")
    val expectedDate = Tomorrow
    val expectedTypeToChanges = Map(
      "Added" -> addedChanges,
      "Changed" -> changedChanges
    )

    val actualChangeGroup = ChangeGroup.load(addedChanges ++ changedChanges)
    actualChangeGroup.version shouldBe expectedVersion
    actualChangeGroup.date shouldBe expectedDate
    actualChangeGroup.typeToChanges shouldBe expectedTypeToChanges
  }
}
