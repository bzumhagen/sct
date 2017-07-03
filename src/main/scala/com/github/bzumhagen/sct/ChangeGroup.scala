package com.github.bzumhagen.sct

import java.time.LocalDate

import com.github.zafarkhaja.semver.Version

/** An object for loading changes into a change group using the latest version and date, and mapping all changes by type */
object ChangeGroup {
  def load(changes: Seq[ChangelogChange]): Option[ChangeGroup] = {
    if(changes.nonEmpty) {
      val latestChange = changes.maxBy(_.version)
      Some(
        ChangeGroup(
          version = latestChange.version,
          date = latestChange.date,
          typeToChanges = changes.groupBy(_.changeType)
        )
      )
    } else {
      None
    }
  }
}

/** A group of changes, grouped by type, with a group version and date
  *
  *  @constructor create a new changelog group with a version, date, and map of change type to changes.
  *  @param version group version
  *  @param date group date
  *  @param typeToChanges map of changeType of a sequence of changes
  */
case class ChangeGroup(version: Version, date: LocalDate, typeToChanges: Map[String, Seq[ChangelogChange]])