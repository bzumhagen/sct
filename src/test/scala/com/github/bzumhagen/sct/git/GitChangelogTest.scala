package com.github.bzumhagen.sct.git

import better.files.File
import com.github.bzumhagen.sct.TestUtils._
import com.github.bzumhagen.sct.{ChangelogChange, ChangelogConfiguration}
import com.github.zafarkhaja.semver.Version
import org.scalatest.{FlatSpec, Matchers}

class GitChangelogTest extends FlatSpec with Matchers {
  private val DefaultConfiguration = ChangelogConfiguration.load()
  private val DefaultDescription = "My Default Change"
  private val Yesterday = Today.minusDays(1)

  "GitChangelog" should "get changes in git repository" in {
    val (dir, repo) = initializeTemporaryGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration, dir)
    val expectedChange = ChangelogChange(DefaultDescription, Version.valueOf("1.0.0"), "added", Today, Some("XYZ-123"))

    commitToRepo(repo, expectedChange.description, expectedChange.version, expectedChange.changeType, expectedChange.reference)

    gitChangelog.getChanges shouldBe Seq(expectedChange)
  }

  "GitChangelog" should "skip changes without version or tag" in {
    val (dir, repo) = initializeTemporaryGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration, dir)
    val expectedChange = ChangelogChange(DefaultDescription, Version.valueOf("1.0.0"), "added", Today, Some("XYZ-123"))

    commitToRepoWithoutTag(repo, "Some change without a tag", Version.valueOf("1.0.0"), "")
    commitToRepoWithoutVersion(repo, "Some change without a version", "added", "")
    commitToRepo(repo, expectedChange.description, expectedChange.version, expectedChange.changeType, expectedChange.reference)
    commitToRepoWithOnlyDescription(repo, "Some change with only a description")

    gitChangelog.getChanges shouldBe Seq(expectedChange)
  }

  it should "generate a smartGrouped markdown file properly" in {
    val (dir, repo) = initializeTemporaryGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration.copy(smartGrouping = true), dir)
    val changes = Seq(
      ChangelogChange("Create project", Version.valueOf("1.0.0"), "Added", Today),
      ChangelogChange("Add some functionality", Version.valueOf("1.1.0"), "Added", Today),
      ChangelogChange("Deprecate some functionality", Version.valueOf("1.1.1"), "Deprecated", Today),
      ChangelogChange("Remove some deprecated functionality", Version.valueOf("2.0.0"), "Removed", Today),
      ChangelogChange("Change some behavior", Version.valueOf("2.0.1"), "Changed", Today),
      ChangelogChange("Change some behavior again", Version.valueOf("2.0.2"), "Changed", Today),
      ChangelogChange("Change some behavior a third time", Version.valueOf("2.0.3"), "Changed", Today),
      ChangelogChange("Add some more functionality", Version.valueOf("2.1.0"), "Added", Today),
      ChangelogChange("Change some behavior yet again", Version.valueOf("2.1.1"), "Changed", Today)
    )
    val changelogFile = File.newTemporaryFile("changelogTestFile")
    val expectedMarkdown =
      s"""# SCT Change Log
        |All notable changes to this project will be documented in this file.
        |
        |The format is based on [Keep a Changelog](http://keepachangelog.com/)
        |and this project adheres to [Semantic Versioning](http://semver.org/).
        |
        |## [2.1.1] - $Today
        |### Changed
        |- Change some behavior yet again
        |***
        |## [2.1.0] - $Today
        |### Added
        |- Add some more functionality
        |### Changed
        |- Change some behavior a third time
        |- Change some behavior again
        |- Change some behavior
        |***
        |## [2.0.0] - $Today
        |### Added
        |- Add some functionality
        |### Removed
        |- Remove some deprecated functionality
        |### Deprecated
        |- Deprecate some functionality
        |***
        |## [1.0.0] - $Today
        |### Added
        |- Create project
        |***
        |""".stripMargin

    changes.foreach(change => commitToRepo(repo, change.description, change.version, change.changeType, change.reference))

    val actualChanges = gitChangelog.getChanges
    gitChangelog.generateMarkdown(changelogFile, actualChanges).contentAsString shouldBe expectedMarkdown
  }

  it should "generate a smartGrouped markdown file properly for repositories without major versions" in {
    val (dir, repo) = initializeTemporaryGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration.copy(smartGrouping = true), dir)
    val changes = Seq(
      ChangelogChange("Ability to specify regex patterns for change elements", Version.valueOf("0.2.0"), "Added", Yesterday),
      ChangelogChange("Rewriting in scala and sbt", Version.valueOf("0.3.0"), "Added", Yesterday),
      ChangelogChange("Fix ChangelogConfiguration test", Version.valueOf("0.3.1"), "Changed", Yesterday),
      ChangelogChange("Refactor changelog interface and add markdown generation", Version.valueOf("0.4.0"), "Added", Yesterday),
      ChangelogChange("Add CI support", Version.valueOf("0.4.0"), "Maintenance", Yesterday),
      ChangelogChange("Change CI to only use JDK 8", Version.valueOf("0.4.0"), "Maintenance", Today),
      ChangelogChange("Add code coverage plugins", Version.valueOf("0.4.0"), "Maintenance", Today),
      ChangelogChange("Add test to get to 100% coverage", Version.valueOf("0.4.0"), "Maintenance", Today),
      ChangelogChange("Added smartGrouping functionality", Version.valueOf("0.5.0"), "Added", Today)
    )
    val changelogFile = File.newTemporaryFile("changelogTestFile")
    val expectedMarkdown =
      s"""# SCT Change Log
         |All notable changes to this project will be documented in this file.
         |
        |The format is based on [Keep a Changelog](http://keepachangelog.com/)
         |and this project adheres to [Semantic Versioning](http://semver.org/).
         |
        |## [0.5.0] - ${Today.toString}
         |### Added
         |- Added smartGrouping functionality
         |- Refactor changelog interface and add markdown generation
         |- Rewriting in scala and sbt
         |- Ability to specify regex patterns for change elements
         |### Maintenance
         |- Add test to get to 100% coverage
         |- Add code coverage plugins
         |- Change CI to only use JDK 8
         |- Add CI support
         |### Changed
         |- Fix ChangelogConfiguration test
         |***
         |""".stripMargin

    changes.foreach(change => commitToRepo(repo, change.description, change.version, change.changeType, None))

    val actualChanges = gitChangelog.getChanges
    gitChangelog.generateMarkdown(changelogFile, actualChanges).contentAsString shouldBe expectedMarkdown
  }

  it should "generate a smartGrouped markdown file properly for repositories with a single version" in {
    val (dir, repo) = initializeTemporaryGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration.copy(smartGrouping = true), dir)
    val change = ChangelogChange("Ability to specify regex patterns for change elements", Version.valueOf("1.0.0"), "Added", Today)
    val changelogFile = File.newTemporaryFile("changelogTestFile")
    val expectedMarkdown =
      s"""# SCT Change Log
      |All notable changes to this project will be documented in this file.
      |
      |The format is based on [Keep a Changelog](http://keepachangelog.com/)
      |and this project adheres to [Semantic Versioning](http://semver.org/).
      |
      |## [${change.version}] - ${change.date}
      |### ${change.changeType}
      |- ${change.description}
      |***
      |""".stripMargin

    commitToRepo(repo, change.description, change.version, change.changeType, change.reference)

    val actualChanges = gitChangelog.getChanges
    gitChangelog.generateMarkdown(changelogFile, actualChanges).contentAsString shouldBe expectedMarkdown
  }

  it should "generate a smartGrouped markdown file properly with reference" in {
    val (dir, repo) = initializeTemporaryGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration.copy(smartGrouping = true), dir)
    val change = ChangelogChange("Ability to specify regex patterns for change elements", Version.valueOf("1.0.0"), "Added", Today, Some("XYZ-123"))
    val changelogFile = File.newTemporaryFile("changelogTestFile")
    val expectedMarkdown =
      s"""# SCT Change Log
         |All notable changes to this project will be documented in this file.
         |
         |The format is based on [Keep a Changelog](http://keepachangelog.com/)
         |and this project adheres to [Semantic Versioning](http://semver.org/).
         |
         |## [${change.version}] - ${change.date}
         |### ${change.changeType}
         |- (${change.reference.get.value}) ${change.description}
         |***
         |""".stripMargin

    commitToRepo(repo, change.description, change.version, change.changeType, change.reference)

    val actualChanges = gitChangelog.getChanges
    gitChangelog.generateMarkdown(changelogFile, actualChanges).contentAsString shouldBe expectedMarkdown
  }

  it should "generate a verbose markdown file properly with references" in {
    val (dir, repo) = initializeTemporaryGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration.copy(smartGrouping = false), dir)
    val changes = Seq(
      ChangelogChange("Create project", Version.valueOf("1.0.0"), "Added", Today, Some("XYZ-123")),
      ChangelogChange("Add some functionality", Version.valueOf("1.1.0"), "Added", Today, Some("XYZ-124")),
      ChangelogChange("Deprecate some functionality", Version.valueOf("1.1.1"), "Deprecated", Today, Some("XYZ-125")),
      ChangelogChange("Remove some deprecated functionality", Version.valueOf("2.0.0"), "Removed", Today, Some("XYZ-126")),
      ChangelogChange("Change some behavior", Version.valueOf("2.0.1"), "Changed", Today, Some("XYZ-127"))
    )
    val changelogFile = File.newTemporaryFile("changelogTestFile")
    val expectedMarkdown =
      s"""# SCT Change Log
        |All notable changes to this project will be documented in this file.
        |
        |The format is based on [Keep a Changelog](http://keepachangelog.com/)
        |and this project adheres to [Semantic Versioning](http://semver.org/).
        |
        |## [2.0.1] - $Today
        |### Changed
        |- (XYZ-127) Change some behavior
        |## [2.0.0] - $Today
        |### Removed
        |- (XYZ-126) Remove some deprecated functionality
        |## [1.1.1] - $Today
        |### Deprecated
        |- (XYZ-125) Deprecate some functionality
        |## [1.1.0] - $Today
        |### Added
        |- (XYZ-124) Add some functionality
        |## [1.0.0] - $Today
        |### Added
        |- (XYZ-123) Create project
        |""".stripMargin

    changes.foreach(change => commitToRepo(repo, change.description, change.version, change.changeType, change.reference))

    val actualChanges = gitChangelog.getChanges
    gitChangelog.generateMarkdown(changelogFile, actualChanges).contentAsString shouldBe expectedMarkdown
  }

  it should "generate a verbose markdown file properly without references" in {
    val (dir, repo) = initializeTemporaryGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration.copy(smartGrouping = false), dir)
    val changes = Seq(
      ChangelogChange("Create project", Version.valueOf("1.0.0"), "Added", Today),
      ChangelogChange("Add some functionality", Version.valueOf("1.1.0"), "Added", Today),
      ChangelogChange("Deprecate some functionality", Version.valueOf("1.1.1"), "Deprecated", Today),
      ChangelogChange("Remove some deprecated functionality", Version.valueOf("2.0.0"), "Removed", Today),
      ChangelogChange("Change some behavior", Version.valueOf("2.0.1"), "Changed", Today)
    )
    val changelogFile = File.newTemporaryFile("changelogTestFile")
    val expectedMarkdown =
      s"""# SCT Change Log
         |All notable changes to this project will be documented in this file.
         |
         |The format is based on [Keep a Changelog](http://keepachangelog.com/)
         |and this project adheres to [Semantic Versioning](http://semver.org/).
         |
         |## [2.0.1] - $Today
         |### Changed
         |- Change some behavior
         |## [2.0.0] - $Today
         |### Removed
         |- Remove some deprecated functionality
         |## [1.1.1] - $Today
         |### Deprecated
         |- Deprecate some functionality
         |## [1.1.0] - $Today
         |### Added
         |- Add some functionality
         |## [1.0.0] - $Today
         |### Added
         |- Create project
         |""".stripMargin

    changes.foreach(change => commitToRepo(repo, change.description, change.version, change.changeType, change.reference))

    val actualChanges = gitChangelog.getChanges
    gitChangelog.generateMarkdown(changelogFile, actualChanges).contentAsString shouldBe expectedMarkdown
  }

  it should "fail if no changes are provided to markdown generation" in {
    val gitChangelog = new GitChangelog(DefaultConfiguration, File.newTemporaryDirectory())
    val changelogFile = File.newTemporaryFile("changelogTestFile")

    assertThrows[IllegalArgumentException] {
      gitChangelog.generateMarkdown(changelogFile, Seq())
    }
  }
}
