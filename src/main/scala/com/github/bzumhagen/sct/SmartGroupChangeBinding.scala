package com.github.bzumhagen.sct

import com.github.bzumhagen.sct.ChangeGroup.load
import com.github.zafarkhaja.semver.Version

class SmartGroupChangeBinding(val template: String, val changes: Seq[ChangelogChange]) extends ChangeBinding {
  require(changes.nonEmpty, "Cannot build change bindings without changes")

  override def buildChangeBindings: Map[String, Any] = {
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
    val otherMajorChangeGroups: List[Option[ChangeGroup]] =
      (0 until latestVersion.getMajorVersion - 1).map { majorVersion =>
        load(
          changes.filter { change =>
            change.version.getMajorVersion == majorVersion ||
              change.version == Version.valueOf(s"${majorVersion + 1}.0.0")
          }
        )
      }.toList

    val groups = List(
      load(latestPatchChanges),
      load(latestMinorChanges),
      load(latestMajorChanges)
    ) ::: otherMajorChangeGroups
    Map(
      "changeGroups" -> groups.flatten,
      "changes"      -> changes
    )
  }
}
