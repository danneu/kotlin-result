
# kotlin-result

A simple `Result` monad for Kotlin.

Similar to <https://github.com/kittinunf/Result> except that
`ValueType` and `ErrorType` in `Result<ValueType, ErrorType>` 
are not constrained.

## Install

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

