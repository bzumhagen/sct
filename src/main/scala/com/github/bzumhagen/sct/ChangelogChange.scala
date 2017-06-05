package com.github.bzumhagen.sct

import java.time.LocalDate

import com.github.zafarkhaja.semver.Version

case class ChangelogChange(description: String, version: Version, changeType: String, reference: Option[String], date: LocalDate)