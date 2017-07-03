package com.github.bzumhagen.sct

import better.files.File

/** A trait defining a changelog.
  *
  * A changelog must implement two methods, getChanges and generateMarkdown.
  * getChanges should return a sequence of changelog changes which represent the relevant changes
  * retrieved from the repository.
  * generateMarkdown should take a file and a sequence of changes, and produce a markdown file containing the specified
  * changes.
  */
trait Changelog {
  def getChanges: Seq[ChangelogChange]
  def generateMarkdown(file: File, changes: Seq[ChangelogChange]): File
}
