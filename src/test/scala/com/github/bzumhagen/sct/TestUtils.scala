package com.github.bzumhagen.sct

import java.time.{LocalDate, LocalDateTime}

import better.files.File
import com.github.zafarkhaja.semver.Version
import org.eclipse.jgit.api.Git

object TestUtils {
  val Today: LocalDate = LocalDateTime.now().toLocalDate

  def initializeTemporaryGitRepo: (File, Git) = {
    val gitDir = File.newTemporaryDirectory("changelogTestRepo").deleteOnExit()
    gitDir -> Git.init.setDirectory(gitDir.toJava).call
  }

  def commitToRepo(repo: Git, description: String, version: Version, tag: String, reference: Option[ChangelogReference]): Unit = {
    repo.commit.setAllowEmpty(true).setMessage(
      s"$description\n\nThis is the long form description of my change\n\nversion: $version\ntag: $tag\nresolves: ${reference.map(_.value).getOrElse("")}"
    ).call()
  }

  def commitToRepoWithoutVersion(repo: Git, description: String, tag: String, reference: String): Unit = {
    repo.commit.setAllowEmpty(true).setMessage(
      s"$description\n\nThis is the long form description of my change\n\ntag: $tag\nresolves: $reference"
    ).call()
  }

  def commitToRepoWithoutTag(repo: Git, description: String, version: Version, reference: String): Unit = {
    repo.commit.setAllowEmpty(true).setMessage(
      s"$description\n\nThis is the long form description of my change\n\nversion: $version\nresolves: $reference"
    ).call()
  }

  def commitToRepoWithOnlyDescription(repo: Git, description: String): Unit = {
    repo.commit.setAllowEmpty(true).setMessage(
      s"$description\n\nThis is the long form description of my change"
    ).call()
  }

}
