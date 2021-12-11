# Change Log
All notable changes to this project will be documented in this file. This change
log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.3.0] - 2021-12-11
### Removed
- No longer expose the format string capability of the underlying library as
  using it incorrectly or unintentionally can lead to JVM crashes or unintended
  data being logged.

## [0.2.5-1] - 2019-05-22
### Fixed
- Fixed issue when log message contains `%n`, which would cause `printf` to
  error out which would cause the JVM to crash.

## [0.2.5] - 2019-05-14
### Fixed
- Namespaced keywords are now allowed as field names
- Invalid characters in field names are now replaced with `_`

## [0.2.4] - 2019-02-11
### Fixed
- Logging an error now no longer causes an IllegalStateException to be thrown

## [0.2.3] - 2018-12-13
### Added
- Tests that capture the output from journalctl in order to validate that we
  actually log the expected values

### Fixed
- Docstring formatting
- Wrong key used for setting syslog priority

## [0.2.2] - 2018-08-23
### Added
- Structured logging of hash-maps

[0.2.1]: https://github.com/runejuhl/clj-journal/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/runejuhl/clj-journal/compare/v0.1.0...v0.2.0

<!-- Local Variables: -->
<!-- mode: markdown -->
<!-- End: -->
