package com.github.bzumhagen.sct

import java.time.LocalDate

import com.github.zafarkhaja.semver.Version

object ChangeGroup {
  def load(changes: Seq[ChangelogChange]): ChangeGroup = {
    val latestChange = changes.maxBy(_.version)
    ChangeGroup(
      version = latestChange.version,
      date = latestChange.date,
      typeToChanges = changes.groupBy(_.changeType)
    )
  }
}

case class ChangeGroup(version: Version, date: LocalDate, typeToChanges: Map[String, Seq[ChangelogChange]])