package com.github.bzumhagen.sct

import better.files.File

trait Changelog {
  def getChanges: Seq[ChangelogChange]
  def generateMarkdown(file: File, changes: Seq[ChangelogChange]): File
}
