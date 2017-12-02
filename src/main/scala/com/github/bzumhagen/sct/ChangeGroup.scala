package com.github.bzumhagen.sct

import java.time.LocalDate

import com.github.zafarkhaja.semver.Version

/** An object for loading changes into a change group using the latest version and date, and mapping all changes by type */
object ChangeGroup {

  def load(changes: Seq[ChangelogChange]): Option[ChangeGroup] = {
    if (changes.nonEmpty) {
      val latestChange = changes.maxBy(_.version)
      val changeTypes = changes
        .groupBy(_.changeType)
        .toList
        .map(ChangeType.apply)

      Some(
        ChangeGroup(
          version = latestChange.version,
          date = latestChange.date,
          changeTypes
        )
      )
    } else {
      None
    }
  }
}

/** A group of changes, grouped by type, with a group version and date
  *
  * @constructor create a new changelog group with a version, date, and map of change type to changes.
  * @param version     group version
  * @param date        group date
  * @param changeTypes sequence of changeTypes
  */
case class ChangeGroup(version: Version, date: LocalDate, changeTypes: Seq[ChangeType])

object ChangeType {
  def apply(tuple: (String, Seq[ChangelogChange])): ChangeType = ChangeType(tuple._1, tuple._2)
}

case class ChangeType(name: String, changes: Seq[ChangelogChange])