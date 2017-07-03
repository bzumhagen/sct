package com.github.bzumhagen.sct

/** A trait defining a change binding.
  *
  * A grouping must implement three methods, template, changes, and buildChangeBindings.
  * template should be a string which details which template to load for these change bindings
  * change should be a non-empty sequence of ChangelogChanges
  * buildChangeBindings should return a map of string to any which will be used in the template for binding the variables.
  */
trait ChangeBinding {
  def template: String
  def changes: Seq[ChangelogChange]
  def buildChangeBindings: Map[String, Any]
}
