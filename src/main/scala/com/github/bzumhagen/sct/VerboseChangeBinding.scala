package com.github.bzumhagen.sct

import com.github.bzumhagen.sct.ChangeGroup.load
import com.github.zafarkhaja.semver.Version

class VerboseChangeBinding(val template: String, val changes: Seq[ChangelogChange]) extends ChangeBinding {
  require(changes.nonEmpty, "Cannot build change bindings without changes")

  override def buildChangeBindings: Map[String, Any] = {
    val versionChanges = changes.groupBy(_.version).toSeq.sortWith((v1, v2) => v1._1.greaterThan(v2._1)).map(_._2)

    Map(
      "changeGroups" -> versionChanges.flatMap(load),
      "changes"      -> changes
    )
  }
}
