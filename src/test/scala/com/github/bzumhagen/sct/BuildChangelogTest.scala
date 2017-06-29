package com.github.bzumhagen.sct

import better.files.File
import com.github.bzumhagen.sct.TestUtils._
import com.github.zafarkhaja.semver.Version
import org.scalatest.{FlatSpec, Matchers}

class BuildChangelogTest extends FlatSpec with Matchers {

  "Build changelog" should "build a changelog with default settings when no arguments are provided" in {
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
        |""".stripMargin

    commitToRepo(gitRepo, expectedChange.description, expectedChange.version, expectedChange.changeType, expectedChange.reference.getOrElse(""))
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

  it should "fail when given bad arguments" in {
    assertThrows[UnsupportedOperationException] { // Result type: IndexOutOfBoundsException
      BuildChangelog.main(Array("-b", "bad-argument"))
    }
  }

}
