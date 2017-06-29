name := "sct"

organization := "com.github.bzumhagen"

version := "0.6.0"

scalaVersion := "2.12.1"

crossScalaVersions := Seq("2.10.6", "2.11.11", "2.12.1")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "com.github.zafarkhaja" % "java-semver" % "0.9.0",
  "org.apache.commons" % "commons-lang3" % "3.5",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.8.0.201705170830-rc1",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.scalatra.scalate" %% "scalate-core" % "1.8.0",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "com.github.scopt" %% "scopt" % "3.6.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "junit" % "junit" % "4.12" % Test
)

libraryDependencies <++= scalaVersion ( sv => Seq(betterFiles(sv)) )

def betterFiles(scalaVersion: String) = scalaVersion match {
  case "2.12.1"  => "com.github.pathikrit" %% "better-files" % "3.0.0"
  case "2.11.11" => "com.github.pathikrit" %% "better-files" % "2.17.1"
  case _         => "com.github.pathikrit" %% "better-files" % "2.17.0"
}

coverageEnabled := true

pgpSecretRing := file("local.secring.asc")

pgpPublicRing := file("local.pubring.asc")

publishMavenStyle := true

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("MIT-style" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/bzumhagen"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/bzumhagen/sct"),
    "scm:git@github.com:bzumhagen/sct.git"
  )
)

developers := List(
  Developer(
    id    = "bzumhagen",
    name  = "Ben Zumhagen",
    email = "bzumhagen@gmail.com",
    url   = url("https://github.com/bzumhagen")
  )
)