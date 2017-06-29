package com.github.bzumhagen.sct.git

import java.time.{Instant, ZoneId}

import better.files.File
import com.github.bzumhagen.sct.{ChangeGroup, Changelog, ChangelogChange, ChangelogConfiguration}
import com.github.zafarkhaja.semver.Version
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

import scala.collection.JavaConverters._
import org.fusesource.scalate._
import org.slf4j.LoggerFactory

import scala.util.matching.Regex

class GitChangelog(val config: ChangelogConfiguration, val gitDir: File) extends Changelog {
  private val logger = LoggerFactory.getLogger(classOf[GitChangelog])

  override def getChanges: Seq[ChangelogChange] = {
    val gitRepository = Git.open(gitDir.toJava)
    val gitLog = gitRepository.log().call().asScala

    gitRepository.close()
    gitLog.flatMap(buildChange).toSeq
  }

  override def generateMarkdown(file: File, changes: Seq[ChangelogChange]): File = {
    val engine = new TemplateEngine
    val changeBindings = buildChangeBindings(changes)
    val nameBinding = Map("name" -> config.name)
    val template = if(config.smartGrouping) "/changelogTemplate.ssp" else "/verboseChangelogTemplate.ssp"
    val output = engine.layout(template, nameBinding ++ changeBindings)
    file.writeText(output)
  }

  private def buildChange(commit: RevCommit): Option[ChangelogChange] = {
    val commitMessage = commit.getFullMessage
    val description = commit.getShortMessage
    val version = getVersionFromMessage(commitMessage)
    val tag = getMatchFor(config.tagPattern, commitMessage)
    val reference = getMatchFor(config.referencePattern, commitMessage)
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

  private def buildChangeBindings(
      changes: Seq[ChangelogChange]): Map[String, Any] =
    if (config.smartGrouping) {
      val latestVersion = changes.maxBy(_.version).version
      val latestPatchChanges = changes.filter { change =>
        change.version.getMajorVersion == latestVersion.getMajorVersion &&
        change.version.getMinorVersion == latestVersion.getMinorVersion &&
        change.version.getPatchVersion > 0
      }
      val latestMinorChanges = changes.filter { change =>
        change.version.getMajorVersion == latestVersion.getMajorVersion &&
        (
          (change.version.getMinorVersion == 0 && change.version.getPatchVersion > 0) ||
          (change.version.getMinorVersion > 0 && change.version.getMinorVersion < latestVersion.getMinorVersion) ||
          (change.version.getMinorVersion != 0 && change.version.getMinorVersion == latestVersion.getMinorVersion && change.version.getPatchVersion == 0)
        )
      }
      val latestMajorChanges = changes.filter { change =>
        (
          change.version.getMajorVersion == latestVersion.getMajorVersion - 1 &&
          (change.version.getMinorVersion > 0 || change.version.getPatchVersion > 0)
        ) ||
        (
          change.version.getMajorVersion == latestVersion.getMajorVersion &&
          change.version.getMinorVersion == 0 &&
          change.version.getPatchVersion == 0
        )
      }
      val otherMajorChangeGroups =
        (0 until latestVersion.getMajorVersion - 1).map { majorVersion =>
          ChangeGroup.load(
            changes.filter { change =>
              change.version.getMajorVersion == majorVersion ||
              change.version == Version.valueOf(s"${majorVersion + 1}.0.0")
            }
          )
        }

      Map(
        "latestPatchChangeGroupOption" -> ChangeGroup.load(latestPatchChanges),
        "latestMinorChangeGroupOption" -> ChangeGroup.load(latestMinorChanges),
        "latestMajorChangeGroupOption" -> ChangeGroup.load(latestMajorChanges),
        "otherMajorChangeGroupOptions" -> otherMajorChangeGroups
      )
    } else {
      Map("changes" -> changes)
    }
}
