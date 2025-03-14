
# ctc-guarantee-balance-eis-stub

This is a microservice designed to behave as an EIS stub for Guarantee Balance.

This microservice is in [Beta](https://www.gov.uk/help/beta). The signature may change.

## Prerequisites

- Scala 3.6.4
- Java 21
- sbt  1.9.9
- [Service Manager](https://github.com/hmrc/service-manager)

## Development Setup

Run from the console using: `sbt run` (starts on port 9518 by default)

### Highlighted SBT Tasks
| Task                    | Description                                                                                          | Command                             |
|:------------------------|:-----------------------------------------------------------------------------------------------------|:------------------------------------|
| run                     | Runs the application with the default configured port                                                | ```$ sbt run```                     |
| test                    | Runs the standard unit tests                                                                         | ```$ sbt test```                    |
| it/test                 | Runs the integration tests                                                                           | ```$ sbt it/test ```                |
| dependencyCheck         | Runs dependency-check against the current project. It aggregates dependencies and generates a report | ```$ sbt dependencyCheck```         |
| dependencyUpdates       | Shows a list of project dependencies that can be updated                                             | ```$ sbt dependencyUpdates```       |
| dependencyUpdatesReport | Writes a list of project dependencies to a file                                                      | ```$ sbt dependencyUpdatesReport``` |

## Helpful information

Guides for the related public Common Transit Convention Traders API are on the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/using-the-hub)


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").