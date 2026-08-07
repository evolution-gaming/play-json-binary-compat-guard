# play-json-binary-compat-guard

[![CI](https://github.com/evolution-gaming/play-json-binary-compat-guard/actions/workflows/ci.yml/badge.svg)](https://github.com/evolution-gaming/play-json-binary-compat-guard/actions/workflows/ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/evolution-gaming/play-json-binary-compat-guard/badge.svg)](https://coveralls.io/github/evolution-gaming/play-json-binary-compat-guard)

A test that fails when `play-json` on the runtime classpath is broken. It checks that:

- `play.api.libs.json` and `play.api.libs.functional` classes are not duplicated on the classpath;
- they do not come from the legacy `com.typesafe.play` coordinates (which clash with `org.playframework`);
- `play-json` is not present without `play-functional`;
- `play-json` module initialisers link without a `LinkageError`.

Everything is decided by reflection, there is no dependency on `play-json` itself. With no `play-json` on the
classpath there is nothing to guard, and the test passes.

## Usage

```scala
libraryDependencies += "com.evolution" %% "play-json-binary-compat-guard" % "0.0.1" % Test
```

```scala
package com.evolution.bootstrap

import com.evolution.playjson.PlayJsonBinaryCompatSuite

class PlayJsonBinaryCompatSpec extends PlayJsonBinaryCompatSuite
```

Or use the check directly:

```scala
val problems: List[String] = PlayJsonBinaryCompat.check(getClass.getClassLoader)
```
