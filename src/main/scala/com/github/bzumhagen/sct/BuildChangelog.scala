package com.github.bzumhagen.sct

import better.files.File
import com.github.bzumhagen.sct.git.GitChangelog
import com.typesafe.config.ConfigFactory

/** Object for main execution of the program */
object BuildChangelog extends App {
  case class Arguments(pathToRepository: String = ".", pathToConfiguration: Option[String] = None)

  @throws[UnsupportedOperationException]
  override def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Arguments]("sct") {
      opt[String]('r', "repository").valueName("<repository-path>").action( (value, config) =>
        config.copy(pathToRepository = value)
      )
      opt[String]('c', "config").optional().valueName("<config-path>").action( (value, config) =>
        config.copy(pathToConfiguration = Some(value))
      )
    }

    parser.parse(args, Arguments()) match {
      case Some(config) => generateChangelog(config)
      case None         => throw new UnsupportedOperationException("Invalid arguments")
    }
  }

  /** Generate a changelog file given some arguments.
    *
    *  @param arguments arguments for generating a changelog file
    */
  def generateChangelog(arguments: Arguments): Unit = {
    val config =
      if(arguments.pathToConfiguration.isDefined) {
        val customConfig = ConfigFactory.parseFile(File(arguments.pathToConfiguration.get).toJava)
        ConfigFactory.load(customConfig)
      } else {
        ConfigFactory.load()
      }
    val changelogConfig = ChangelogConfiguration.load(config)

    val gitChangelog = new GitChangelog(changelogConfig, File(arguments.pathToRepository))
    val changes = gitChangelog.getChanges

    gitChangelog.generateMarkdown(File("changelog.md"), changes)
  }

}
