package com.github.bzumhagen.sct.git

import java.time.{Instant, ZoneId}

import better.files.File
import com.github.bzumhagen.sct.{Changelog, ChangelogChange, ChangelogConfiguration}
import com.github.zafarkhaja.semver.Version
import com.typesafe.scalalogging.Logger
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

import scala.collection.JavaConverters._
import org.fusesource.scalate._

import scala.util.matching.Regex

class GitChangelog(val config: ChangelogConfiguration, val gitDir: File) extends Changelog {
  private val logger = Logger[GitChangelog]

  case class MyVersion(version: String, date: String, tag: String, description: String)

  override def getChanges: Seq[ChangelogChange] = {
    val gitRepository = Git.open(gitDir.toJava)
    val gitLog = gitRepository.log().call().asScala

    gitLog.flatMap(buildChange).toSeq
  }

  override def generateMarkdown(file: File, changes: Seq[ChangelogChange]): File = {
    val engine = new TemplateEngine
    val binding = Map("changes" -> changes)
    val output = engine.layout("/changelogTemplate.ssp", binding)
    file.writeText(output)
  }

  private def buildChange(commit: RevCommit): Option[ChangelogChange] = {
    val commitMessage = commit.getFullMessage
    val description = commit.getShortMessage
    val version = getVersionFromMessage(commitMessage)
    val tag = getMatchFor(config.tagPattern, commitMessage)
    val reference = getMatchFor(config.referencePattern, commitMessage)
    val date = Instant.ofEpochSecond(commit.getCommitTime).atZone(ZoneId.systemDefault).toLocalDate
    val dateTime = Instant.ofEpochSecond(commit.getCommitTime).atZone(ZoneId.systemDefault)

    (version, tag) match {
      case (Some(v), Some(t)) =>
        Some(ChangelogChange(description, v, t, reference, date))
      case (None, Some(_))    =>
        logger.debug(s"Didn't find version in commit message [$commitMessage]")
        None
      case (Some(_), None)    =>
        logger.debug(s"Didn't find tag in commit message [$commitMessage]")
        None
      case _                  =>
        logger.debug(s"Didn't find tag or version in commit message [$commitMessage]")
        None
    }
  }

  private def getVersionFromMessage(commitMessage: String): Option[Version] = {
    val version = getMatchFor(config.versionPattern, commitMessage)
    version.map(Version.valueOf)
  }

  private def getMatchFor(regex: Regex, data: String): Option[String] = data match {
    case regex(matchingValue) => Some(matchingValue)
    case _                    => None
  }
}
