
# kotlin-result [![Jitpack](https://jitpack.io/v/com.danneu/kotlin-result.svg)](https://jitpack.io/#com.danneu/kotlin-result) [![Build Status](https://travis-ci.org/danneu/kotlin-result.svg?branch=master)](https://travis-ci.org/danneu/kotlin-result)

A simple `Result` monad for Kotlin.

Similar to <https://github.com/kittinunf/Result> except that
`ValueType` and `ErrorType` in `Result<ValueType, ErrorType>` 
are not constrained.

Extracted from [kog][kog].

[kog]: https://github.com/danneu/kog

## Install [![Jitpack](https://jitpack.io/v/com.danneu/kotlin-result.svg)](https://jitpack.io/#com.danneu/kotlin-result)

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile "com.danneu:kotlin-result:x.y.z"
    // Or always get latest
    compile "com.danneu:kotlin-result:master-SNAPSHOT"
}
```

## Usage

```kotlin
import com.danneu.result.Result

Result.ok(100).map { it + 1 } == Result.ok(101)
Result.err("failure").map { it + 1 } == Result.err("failure")
```

To represent a Result that can never fail, use Kotlin's `Never`:

```kotlin
fun add (a: Int, b: Int): Result<Int, Never> {
    return Result.ok(a + b)
}
```

