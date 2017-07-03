package com.github.bzumhagen.sct

/**
  * Created by zumhagen on 7/3/17.
  */
class VerboseChangeBinding(val template: String, val changes: Seq[ChangelogChange]) extends ChangeBinding {
  require(changes.nonEmpty, "Cannot build change bindings without changes")

  override def buildChangeBindings: Map[String, Any] = {
    Map("changes" -> changes)
  }
}
