package com.github.bzumhagen.sct

import java.time.LocalDate

import com.github.zafarkhaja.semver.Version

/** A changelog change
  *
  * @param description change description
  * @param version change version
  * @param changeType change type (i.e. Added)
  * @param reference change reference (i.e. XYZ-123)
  * @param date change date
  */
object ChangelogChange {
  def apply(description: String, version: Version, changeType: String, date: LocalDate, reference: Option[String] = None) =
    new ChangelogChange(description, version, changeType, reference.map(ChangelogReference), date)
}
case class ChangelogChange(description: String, version: Version, changeType: String, reference: Option[ChangelogReference], date: LocalDate)

case class ChangelogReference(value: String)