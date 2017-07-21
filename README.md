# sct [![Build Status](https://travis-ci.org/bzumhagen/sct.svg?branch=master)](https://travis-ci.org/bzumhagen/sct) [![Coverage Status](https://coveralls.io/repos/github/bzumhagen/sct/badge.svg?branch=master)](https://coveralls.io/github/bzumhagen/sct?branch=master)
This repository houses the source code for sct, the simple changelog tool. It's a tool which allows a changelog to be generated based on version control system commits. We already should be writing good commit messages, so why not go a little farther, write commit messages a little better, and then automatically generate a changelog on demand.

Currently sct only supports projects which are using git and semantic versioning.

## Getting Started

Currently there are two main ways you can use sct.

1. Via build tool plugin - This is the recommended way to use sct. See https://github.com/bzumhagen/sbt-sct for a plugin for sbt. Maven plugin not currently available.
2. Via source / source assembly - If you want to go this route, check out the rest of the steps below.

### Prerequisites

* Java 8
* SBT

```
brew install sbt
```

### Installing

Clone the repository

```
git clone https://github.com/bzumhagen/sct.git
```
CD into the project directory

```
cd sct
```
Compile the project

```
sbt compile
```

Run the project

```
sbt run
```

View the generated changelog

```
cat changelog.md
```

## Running the tests

```
sbt test
```

## Building jar with dependencies / assembly

``` 
sbt assembly
```

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

No contributing guide yet.

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Authors

* **Ben Zumhagen** - *Initial work* - [Github](https://github.com/bzumhagen)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Thanks to [KeepAChangelog](http://keepachangelog.com/en/1.0.0/) for the formatting inspiration. Sorry I based the project around breaking the rule at the top of your page "Don’t let your friends dump git logs into changelogs."

