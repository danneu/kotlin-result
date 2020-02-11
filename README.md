Note: Kotlin has since added `Result` to its stdlib: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html

# kotlin-result [![Jitpack](https://jitpack.io/v/com.danneu/kotlin-result.svg)](https://jitpack.io/#com.danneu/kotlin-result) [![Build Status](https://travis-ci.org/danneu/kotlin-result.svg?branch=master)](https://travis-ci.org/danneu/kotlin-result)

A simple `Result` monad for Kotlin.

Extracted from [kog](https://github.com/danneu/kog).

## Table of Contents

<!-- toc -->

- [Install](#install-)
- [Usage](#usage)
  * [Creating Results](#creating-results)
  * [Unwrapping Results](#unwrapping-results)
    + [`.getOrThrow()`](#getorthrow)
    + [`.getOrElse()`](#getorelse)
  * [Transforming Results](#transforming-results)
    + [`.map()`](#map)
    + [`.mapError()`](#maperror)
    + [`.fold()`](#fold)
    + [`.flapMap()`](#flapmap)
    + [`.flatMapError()`](#flatmaperror)
  * [Combining Results](#combining-results)
    + [`Result.all()`](#resultall)
  * [Representing Impossible Failure](#representing-impossible-failure)
- [Versus kittinunf/Result](#versus-kittinunfresult)

<!-- tocstop -->

## Install <a href="https://jitpack.io/#com.danneu/kotlin-result"><img src="https://jitpack.io/v/com.danneu/kotlin-result.svg"></a>

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

A `Result` represents an outcome of an operation that can succeed or fail.

- `Result.Ok` wraps the output value: `result.value`
- `Result.Err` wraps the output error: `result.error`

The purpose of the abstraction is to provide a more functional
and composable way to handle errors than `try/catch`.

For an example, check out [kittinunf/Result#why](https://github.com/kittinunf/Result#why).

### Creating Results

```kotlin
val okResult = Result.ok(42)
val errResult = Result.err("failure")
```

### Unwrapping Results

#### `.getOrThrow()`

Force-unwrap a result value.

```kotlin
Result.ok(42).getOrThrow() == 42
Result.err("failure").getOrThrow() // throws com.danneu.result.UnwrapException
```

#### `.getOrElse()`

Unwrap a result value with a fallback in case of error.

```kotlin
Result.ok(42).getOrElse(-1) == 42
Result.err("failure").getOrElse(-1) == -1
```

Or pass in a function if you want to inspect or transform the error.

```kotlin
Result.err("failure").getOrElse { message ->
    message + "-transformed"
} == "failure-transformed"
```

### Transforming Results

#### `.map()`

Transform a result's value.

```kotlin
Result.ok(100).map { it + 1 } == Result.ok(101)
Result.err("failure").map { it + 1 } == Result.err("failure")
```

#### `.mapError()`

Transform a result's error.

```kotlin
Result.ok(100).mapError { it + "-mapped" } == Result.ok(100)
Result.err("failure").mapError { it + "-mapped" } == Result.err("failure-mapped")
```

#### `.fold()`

Reduce both sides into a final value.

```kotlin
Result.ok(100).fold({ it + 1 }, { it + "-mapped" }) == 101
Result.err("failure").fold({ it + 1 }, { -1 }) == -1
```

#### `.flatMap()`

Transform result into a new result based on its value.

Like `.map()` except that the lambda returns a new result instead of a new value.

```kotlin
Result.ok(42).flatMap { Result.ok(100) } == Result.ok(100)
Result.err("failure").flatMap { Result.ok(100) } == Result.err("failure")
```

#### `.flatMapError()`

Transform result into a new result based on its error.

Like `.mapError()` except that the lambda returns a new result instead of a new error.

```kotlin
Result.ok(42).flatMapError { Result.ok(100) } == Result.ok(42)
Result.err("failure").flatMapError { Result.ok(100) } == Result.ok(100)
```

### Combining Results

#### `Result.all()`

Combine a list of results into a single result.

Short-circuits on first error.

```kotlin
Result.all(ok(1), ok(2), ok(3)) == Result.ok([1, 2, 3])
Result.all(ok(1), err("failure"), ok(3)) == Result.err("failure")
```

### Representing Impossible Failure

To represent a Result that can never fail, use Kotlin's `Never`:

```kotlin
fun add (a: Int, b: Int): Result<Int, Never> {
    return Result.ok(a + b)
}
```

## Versus <a href="https://github.com/kittinunf/Result">kittinunf/Result</a>

[kittinunf/Result][kittinunf] is another Result monad library
implemented for Kotlin. However, it constrains
its Result value to non-null values and its error to instances
of `Exception`.

This library does not constrain either types. This means that it can
model nullable result values and errors represented as any type,
like a simple string error message or an integer error code.

For example, you can represent `Result<User?, String>` in this library
but not in [kittinunf/Result][kittinunf].

[kittinunf]: https://github.com/kittinunf/Result
