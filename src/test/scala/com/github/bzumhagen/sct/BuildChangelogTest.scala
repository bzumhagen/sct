package com.github.bzumhagen.sct

import better.files.File
import com.github.bzumhagen.sct.TestUtils._
import com.github.zafarkhaja.semver.Version
import org.scalatest.{FlatSpec, Matchers}

class BuildChangelogTest extends FlatSpec with Matchers {

  behavior of "BuildChangelog"

  it should "build a changelog with default settings when no arguments are provided" in {
    val expectedFile = File("changelog.md")
    val expectedContentBeginning =
        """# SCT Change Log
          |All notable changes to this project will be documented in this file.
          |
          |The format is based on [Keep a Changelog](http://keepachangelog.com/)
          |and this project adheres to [Semantic Versioning](http://semver.org/).
          |""".stripMargin
    BuildChangelog.main(Array())
    expectedFile.contentAsString startsWith expectedContentBeginning
    expectedFile.delete()
  }

  it should "build a changelog from a custom repository path" in {
    val (repoDir, gitRepo) = initializeTemporaryGitRepo
    val expectedChange = ChangelogChange("My Change", Version.valueOf("1.0.0"), "Added", None, Today)
    val expectedFile = File("changelog.md")
    val expectedContent =
      s"""# SCT Change Log
        |All notable changes to this project will be documented in this file.
        |
        |The format is based on [Keep a Changelog](http://keepachangelog.com/)
        |and this project adheres to [Semantic Versioning](http://semver.org/).
        |
        |## [${expectedChange.version}] - ${expectedChange.date}
        |### ${expectedChange.changeType}
        |- ${expectedChange.description}
        |***
        |""".stripMargin

    commitToRepo(gitRepo, expectedChange.description, expectedChange.version, expectedChange.changeType, expectedChange.reference)
    BuildChangelog.main(Array("-r", repoDir.pathAsString))

    expectedFile.contentAsString shouldBe expectedContent
    expectedFile.delete()
  }

  it should "build a changelog with a custom configuration but keep defaults for anything not overridden" in {
    val customProjectName = "My Custom Project"
    val customConfigFile = File.newTemporaryFile("myCustomConfig").writeText(
      s"""
        |sct {
        | name = "$customProjectName"
        |}
      """.stripMargin
    )

    val expectedFile = File("changelog.md")
    val expectedContentBeginning =
      s"""# $customProjectName Change Log
        |All notable changes to this project will be documented in this file.
        |
        |The format is based on [Keep a Changelog](http://keepachangelog.com/)
        |and this project adheres to [Semantic Versioning](http://semver.org/).
        |""".stripMargin
    BuildChangelog.main(Array("-c", customConfigFile.pathAsString))
    expectedFile.contentAsString startsWith expectedContentBeginning
    expectedFile.delete()
  }

  it should "build a changelog given a start version" in {
    val (repoDir, gitRepo) = initializeTemporaryGitRepo
    val expectedChange1 = ChangelogChange("My 0.1.0 Change", Version.valueOf("0.1.0"), "Added", None, Today)
    val expectedChange2 = ChangelogChange("My 1.0.0 Change", Version.valueOf("1.0.0"), "Added", None, Today)
    val expectedChange3 = ChangelogChange("My 1.1.0 Change", Version.valueOf("1.1.0"), "Added", None, Today)
    val expectedChange4 = ChangelogChange("My 1.2.0 Change", Version.valueOf("1.2.0"), "Added", None, Today)
    val expectedFile = File("changelog.md")
    val expectedContent =
      s"""# SCT Change Log
         |All notable changes to this project will be documented in this file.
         |
         |The format is based on [Keep a Changelog](http://keepachangelog.com/)
         |and this project adheres to [Semantic Versioning](http://semver.org/).
         |
         |## [${expectedChange4.version}] - ${expectedChange4.date}
         |### ${expectedChange4.changeType}
         |- ${expectedChange4.description}
         |- ${expectedChange3.description}
         |***
         |""".stripMargin

    commitToRepo(gitRepo, expectedChange1.description, expectedChange1.version, expectedChange1.changeType, expectedChange1.reference)
    commitToRepo(gitRepo, expectedChange2.description, expectedChange2.version, expectedChange2.changeType, expectedChange2.reference)
    commitToRepo(gitRepo, expectedChange3.description, expectedChange3.version, expectedChange3.changeType, expectedChange3.reference)
    commitToRepo(gitRepo, expectedChange4.description, expectedChange4.version, expectedChange4.changeType, expectedChange4.reference)
    BuildChangelog.main(Array("-r", repoDir.pathAsString, "-v", "1.0.0"))

    expectedFile.contentAsString shouldBe expectedContent
    expectedFile.delete()
  }

  it should "fail when given a badly formatted start version" in {
    assertThrows[UnsupportedOperationException] { // Result type: IndexOutOfBoundsException
      BuildChangelog.main(Array("-v", "1.1-SNAPSHOTX"))
    }
  }

  it should "fail when given an empty start version" in {
    assertThrows[UnsupportedOperationException] { // Result type: IndexOutOfBoundsException
      BuildChangelog.main(Array("-v", ""))
    }
  }

  it should "fail when given bad arguments" in {
    assertThrows[UnsupportedOperationException] { // Result type: IndexOutOfBoundsException
      BuildChangelog.main(Array("-b", "bad-argument"))
    }
  }

}
