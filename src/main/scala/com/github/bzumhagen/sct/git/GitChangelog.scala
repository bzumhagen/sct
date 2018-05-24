package com.github.bzumhagen.sct.git

import java.time.{Instant, ZoneId}

import better.files.File
import com.github.bzumhagen.sct._
import com.github.zafarkhaja.semver.Version
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.fusesource.scalate._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.matching.Regex

/** A changelog for a git repository.
  *
  *  @constructor create a new git changelog with a config and directory.
  *  @param config changelog configuration
  *  @param gitDir directory containing the git repository
  */
class GitChangelog(val config: ChangelogConfiguration, val gitDir: File) extends Changelog {
  private val logger = LoggerFactory.getLogger(classOf[GitChangelog])

  /** Get all changes from git repository which match the mandatory criteria (i.e. contain version and specified tag) */
  override def getChanges: Seq[ChangelogChange] = {
    val gitRepository = Git.open(gitDir.toJava)
    val gitLog = gitRepository.log().call().asScala

    gitRepository.close()
    gitLog.flatMap(buildChange).toSeq
  }

  /** Generate a file given a file and a non-empty sequence of changes.
    *
    *  @param file file to write markdown into
    *  @param changes sequence of changes to build changelog with
    */
  override def generateChangelog(file: File, changes: Seq[ChangelogChange]): File = {
    require(changes.nonEmpty, "Cannot generate changelog without changes")

    val engine = new TemplateEngine
    val changeBinding = new SmartGroupChangeBinding(getTemplate, changes)
    val defaultBindings = Map(
      "name" -> config.name,
      "showReference" -> config.showReference
    )
    val output = engine.layout(changeBinding.template, defaultBindings ++ changeBinding.buildChangeBindings)
    file.writeText(output)
  }

  private def buildChange(commit: RevCommit): Option[ChangelogChange] = {
    val commitMessage = commit.getFullMessage
    val description = commit.getShortMessage
    val version = getVersionFromMessage(commitMessage)
    val tag = getMatchFor(config.tagPattern, commitMessage)
    val reference = getMatchFor(config.referencePattern, commitMessage).map(ChangelogReference)
    val date = Instant
      .ofEpochSecond(commit.getCommitTime)
      .atZone(ZoneId.systemDefault)
      .toLocalDate

    (version, tag) match {
      case (Some(v), Some(t)) =>
        Some(ChangelogChange(description, v, t, reference, date))
      case (None, Some(_)) =>
        logger.debug(s"Didn't find version in commit message [$commitMessage]")
        None
      case (Some(_), None) =>
        logger.debug(s"Didn't find tag in commit message [$commitMessage]")
        None
      case _ =>
        logger.debug(
          s"Didn't find tag or version in commit message [$commitMessage]")
        None
    }
  }

  private def getVersionFromMessage(commitMessage: String): Option[Version] = {
    val version = getMatchFor(config.versionPattern, commitMessage)
    version.map(Version.valueOf)
  }

  private def getMatchFor(regex: Regex, data: String): Option[String] =
    data match {
      case regex(matchingValue) => Some(matchingValue)
      case _ => None
    }

  private def getTemplate =
    config.templateFile.getOrElse(
      if (config.smartGrouping) {
        "/templates/markdown.mustache"
      } else {
        "/templates/verbose_markdown.mustache"
      })
}
