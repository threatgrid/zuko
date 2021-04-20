# Zuko [![Build Status](https://travis-ci.org/threatgrid/zuko.svg?branch=main)](https://travis-ci.org/threatgrid/zuko) [![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.md)

This is a Clojure/ClojureScript library that works with other Threatgrid data applications.

[![Clojars Project](http://clojars.org/org.clojars.quoll/zuko/latest-version.svg)](http://clojars.org/org.clojars.quoll/zuko)

The initial applications using this library are
[Naga](https://github.com/threatgrid/naga) and [Asami](https://github.com/threatgrid/asami).

This library provides functionality for:
- Managing data (especially JSON data) in a graph database. Asami describes this in the [wiki entry for Entity Structures](https://github.com/threatgrid/asami/wiki/Entity-Structure).
- Projecting data into tuples suitable for query results or insertion into a graph database. Generation of this data with Zuko is discussed in [Asami](https://github.com/threatgrid/asami/wiki/Entity-Structure).
- Logging in Clojure or ClojureScript
- General utilities to avoid duplicating code between projects.

## Usage

Include a dependency to this library.

In Leiningen:

```clojure
[org.clojars.quoll/zuko "0.6.4"]
```

In `deps.edn`:

```clojure
{
  :deps {
    org.clojars.quoll/zuko {:mvn/version "0.6.4"}
  }
}
```

### Logging
The main general purpose code here is the simple logging facility.

#### Clojure
By default in Clojure, logging function are disabled, which means that
using log functions will not create any code.
This ensures that a code path is unaffacted when logging is turned off.
The easiest way to ensure that logging is enabled is to set `logging.enabled`
in the System Properties:
```
$ clj -J-Dlogging.enabled=true
```
Alternatively, it can be set programatically if you're loading the logging namespace
dynamically. This may be important to load a namespace that uses logging that you
don't want to modify:

```clojure
(System/setProperty "logging.enabled" "true")
(require '[zuko.logging :as log])
```
If you want to ensure logging occurs in your own code, then just enable it:
```clojure
(require '[zuko.logging :as log])
(log/set-enabled! true)
```
The "enabled" flag is global across all namespaces.

#### ClojureScript
By default, ClojureScript logging is enabled. This reflects the fact that
ClojureScript is typically built and deployed, so it is not usually possible to
update a flag to modify actual inlined code.

If you want to ensure that logging is excluded from the final output, then ensure that
your ClojureScript code is compiled with `-Dlogging.enabled=false`

### Usage
Logging always checks the current logging level before emitting a log. This can be done
dynamically with a number or using the `set-logging-level!` function, which can take
a number or a keyword.
```clojure
(ns my.program (:require [zuko.logging :as log]))

(log/set-logging-level! :warn)
(log/error "Something went wrong!")
(log/info "This does nothing")
;; temporarily allow through INFO messages
(binding [log/*level* 4]
  (log/info "The level was set to:" log/*level*))
```
This will output:
```
my.program WARN: Something went wrong!
my.program INFO: The level was set to:4
```
#### Numerical levels
The logging levels are:
| Keyword  | # |
|----------|---|
| `:fatal` | 1 |
| `:error` | 2 |
| `:warn`  | 3 |
| `:info`  | 4 |
| `:debug` | 5 |
| `:trace` | 6 |

Logging will record any messages that are at the current level or lower. So when level 4
is set (info), then all `info`, `warn`, `error` and `fatal` messages are recorded.

Bindings must be done by numerical constant, but using `set-logging-level!` can use a number
or the above keywords.

#### Output
If no output has been set, then all logging will go to stdout by default (or the console in a web browser).

Output can be set dynamically, or by using the `set-output!` function. Available options are:

- `nil` or `""`: write to standard output or console.
- "filename": appends to a file. This works in Clojure or ClojureScript on Node.js.
- Atom: data will be added via `conj`
- `java.io.Writer`: sent to the writer (Clojure only).

```clojure
(def output (atom []))
(log/set-output! output)
(log/debug "step 1")
(binding [log/*output* nil]
  (log/debug "step 2"))
(log/debug "step 3")
(deref output)
```
This will return:
```
["my.program DEBUG: step 1" "my.program DEBUG: step 3"]
```
and standard out will show:
```
my.program DEBUG: step 2
```

### I/O
Zuko provides some simple cross-platform I/O operations.

#### `(spit path data)` and `(slurp path)`
Writes data to and reads data from a resource identified by `path`. The behavior depends on the platform:
- On the JVM these are just aliases for `clojure.core/spit` and `clojure.core/slurp`.
- On Node.js, these operations work the same as the JVM version.
- In a web browser, data is stored to and retrieved from `localStorage`.

#### `(exists path)`
Returns `true` when the resource identified by `path` exists.
`path` typically represents a file path. In a web browser, this indicates a `localStorage` entry.

#### `(rm path)`
Removes the resource identified by `path`. Returns `true` if the resource existed, or `false`/`nil` otherwise.

## License

Copyright © 2020-2021 Cisco Systems

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
