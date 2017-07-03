package com.github.bzumhagen.sct

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
  }
}
