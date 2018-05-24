package com.github.bzumhagen.sct

import better.files.File

/** A trait defining a changelog.
  *
  * A changelog must implement two methods, getChanges and generateChangelog.
  * getChanges should return a sequence of changelog changes which represent the relevant changes retrieved from the repository.
  * generateChangelog should take a file and a sequence of changes, and produce a file containing the specified changes.
  */
trait Changelog {
  def getChanges: Seq[ChangelogChange]
  def generateChangelog(file: File, changes: Seq[ChangelogChange]): File
}
