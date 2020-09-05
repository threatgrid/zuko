# Change Log

## [Unreleased]
### Changed


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

[Unreleased]: https://github.com/threatgrid/zuko/compare/0.3.1...HEAD
[0.3.1]: https://github.com/threatgrid/zuko/compare/0.3.0...0.3.1
[0.3.0]: https://github.com/threatgrid/zuko/compare/0.2.4...0.3.0
[0.2.4]: https://github.com/threatgrid/zuko/compare/0.2.3...0.2.4
