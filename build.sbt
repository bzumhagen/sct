name := "sct"

organization := "com.github.bzumhagen"

version := "0.2.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "com.github.zafarkhaja" % "java-semver" % "0.9.0",
  "org.apache.commons" % "commons-lang3" % "3.5",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.8.0.201705170830-rc1",
  "com.github.pathikrit" %% "better-files" % "3.0.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.scalatra.scalate" %% "scalate-core" % "1.8.0",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "junit" % "junit" % "4.12" % Test
)

coverageEnabled := true
