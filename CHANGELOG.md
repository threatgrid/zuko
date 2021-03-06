# Change Log

## [Unreleased]

## [0.6.5] - 2021-05-27
### Changed
- Array processing is no longer recursive when converting entities to tuples.

## [0.6.4] - 2021-04-20
### Changed
- Renamed `:tg/sub` to `:tg/owns` before any dependencies lock this name in.

### Added
- Fixed entity references to top level entities to not own them

## [0.6.3] - 2021-04-16
### Added
- Entity encoding now includes new attributes that connect sub-entities to top-level entities.

### Changed
- Updated to Naga-store 0.5.3

## [0.6.2] - 2021-04-08
### Changed
- Updated to ClojureScript 1.10.844

### Fixed
- Errors in the README

## [0.6.1] - 2021-03-31
### Fixed
- localStore option for i/o.

## [0.6.0] - 2021-03-30
### Added
- I/O operations.

## [0.5.1] - 2021-03-20
### Changed
- Allowing arbitrary keys for entities.

## [0.5.0] - 2021-03-19
### Changed
- When building entities temp-ids no longer map newly allocated nodes for entities without an id back to themselves.

### Removed
- No longer reading or writing JSON entities. This should be done outside of Zuko. Affected functions are:
  * `zuko.entity.reader/graph->str`
  * `zuko.entity.writer/stream->triples`
- No more dependency on Cheshire.

## [0.4.6] - 2021-03-12
### Added
- Lookup refs in entity values.

## [0.4.5] - 2021-03-11
### Changed
- Datatype testing with schema now allows various datatypes throughout query patterns.

## [0.4.4] - 2021-03-09
### Changed
- Datatype testing with schema now allows various datatypes as entity nodes.

## [0.4.3] - 2021-03-09
### Added
- Whitelist for function resolution.

### Changed
- Entity structures now check for substructures on anything that is a valid node.

## [0.4.2] - 2021-02-19
### Fixed
- Workaround for the Clojure `#{}` reader macro from disallowing duplicates.

## [0.4.1] - 2021-02-18
### Changed
- Removed unnecessary ClojureScript dependencies from artifacts.

## [0.4.0] - 2021-02-03
### Added
- Added an internal logging library for use in both clj and cljs

### Fixed
- Removing duplicates when projecting for new entities


## [0.3.4] - 2021-01-19
### Added
- Updated Results to allow for symbols.

## [0.3.3] - 2020-12-14
### Added
- Changed entity conversion to triples to use fewer resources.


## [0.3.2] - 2020-11-09
### Added
- Added flag to force nesting of objects, even when they are top level.


## [0.3.1] - 2020-09-04
### Added
- Added support for nil values and array entries.

### Fixed
- Now binding id-map for entity updates.
- Removed references to the naga namespace.


## [0.3.0] - 2020-09-03
### Added
- Supporting empty arrays.


## [0.2.4] - 2020-08-19
### Fixed
- Projecting to a tuple from empty results now returns `nil` instead of a vector containing `nil`.


## 0.2.3 - 2020-08-14
### Fixed
- Projecting to a single element tuple is fixed.

### Changed
- Deprecated the `vars` function in schema.

## 0.2.2 - 2020-08-05
### Changed
- Entity read/write now supports multi-arity attributes. These come in and go out as sets of values.

### Fixed
- More consistent `:db/ident` handling.
- Array reading now returns vectors instead of lists.

## 0.1.0 - 2020-07-23
### Changed
- Allowing string attributes

### Added
- Extracted from Asami/Naga

[Unreleased]: https://github.com/threatgrid/zuko/compare/0.6.5...HEAD
[0.6.5]: https://github.com/threatgrid/zuko/compare/0.6.4...0.6.5
[0.6.4]: https://github.com/threatgrid/zuko/compare/0.6.3...0.6.4
[0.6.3]: https://github.com/threatgrid/zuko/compare/0.6.2...0.6.3
[0.6.2]: https://github.com/threatgrid/zuko/compare/0.6.1...0.6.2
[0.6.1]: https://github.com/threatgrid/zuko/compare/0.6.0...0.6.1
[0.6.0]: https://github.com/threatgrid/zuko/compare/0.5.1...0.6.0
[0.5.1]: https://github.com/threatgrid/zuko/compare/0.5.0...0.5.1
[0.5.0]: https://github.com/threatgrid/zuko/compare/0.4.6...0.5.0
[0.4.6]: https://github.com/threatgrid/zuko/compare/0.4.5...0.4.6
[0.4.5]: https://github.com/threatgrid/zuko/compare/0.4.4...0.4.5
[0.4.3]: https://github.com/threatgrid/zuko/compare/0.4.3...0.4.4
[0.4.3]: https://github.com/threatgrid/zuko/compare/0.4.2...0.4.3
[0.4.2]: https://github.com/threatgrid/zuko/compare/0.4.1...0.4.2
[0.4.1]: https://github.com/threatgrid/zuko/compare/0.4.0...0.4.1
[0.4.0]: https://github.com/threatgrid/zuko/compare/0.3.4...0.4.0
[0.3.4]: https://github.com/threatgrid/zuko/compare/0.3.3...0.3.4
[0.3.3]: https://github.com/threatgrid/zuko/compare/0.3.2...0.3.3
[0.3.2]: https://github.com/threatgrid/zuko/compare/0.3.1...0.3.2
[0.3.1]: https://github.com/threatgrid/zuko/compare/0.3.0...0.3.1
[0.3.0]: https://github.com/threatgrid/zuko/compare/0.2.4...0.3.0
[0.2.4]: https://github.com/threatgrid/zuko/compare/0.2.3...0.2.4
