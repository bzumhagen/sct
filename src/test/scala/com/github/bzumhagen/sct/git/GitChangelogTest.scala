package com.github.bzumhagen.sct.git

import better.files.File
import com.github.bzumhagen.sct.{ChangelogChange, ChangelogConfiguration}
import com.github.zafarkhaja.semver.Version
import org.eclipse.jgit.api.Git
import org.scalatest.{FlatSpec, Matchers}

class GitChangelogTest extends FlatSpec with Matchers {
  private val DefaultConfiguration = new ChangelogConfiguration()
  private val DefaultDescription = "My Default Change"

  "GitChangelog" should "get changes in git repository" in {
    val (dir, repo) = initializeGitRepo
    val gitChangelog = new GitChangelog(DefaultConfiguration, dir)
    val expectedChange = ChangelogChange(DefaultDescription, Version.valueOf("1.0.0"), "added", Some("XYZ-123"))

    commitToRepo(repo, expectedChange.description, expectedChange.version, expectedChange.changeType, expectedChange.reference.get)

    gitChangelog.getChanges shouldBe Seq(expectedChange)
  }

  private def initializeGitRepo: (File, Git) = {
    val gitDir = File.newTemporaryDirectory("changelogTestRepo")
    gitDir -> Git.init.setDirectory(gitDir.toJava).call
  }

  private def commitToRepo(repo: Git, description: String, version: Version, tag: String, reference: String): Unit = {
    repo.commit.setAllowEmpty(true).setMessage(
      s"$description\n\nThis is the long form description of my change\n\nversion: $version\ntag: $tag\nresolves: $reference"
    ).call()
  }
}
