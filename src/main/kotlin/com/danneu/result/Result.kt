package com.danneu.result

// These were moved into extension functions since they violated `out` constraint
// when implemented as instance methods.

/** Get result value with a fallback value if result is err.
 *
 * Result.ok(42).getOrElse(-1) == 42
 * Result.err("failure").getOrElse(-1) == -1
 */
fun <V, E> Result<V, E>.getOrElse(default: V): V = when (this) {
    is Result.Ok<V, E> ->
        this.value
    is Result.Err<V, E> ->
        default
}

/** Transform value into a new result.
 *
 * Result.ok(42).flatMap { Result.ok(100) } == Result.ok(100)
 * Result.err("failure").flatMap { Result.ok(100) } == Result.err("failure")
 */
fun <V, V2, E> Result<V, E>.flatMap(transform: (V) -> Result<V2, E>): Result<V2, E> = when (this) {
    is Result.Ok<V, E> ->
        transform(value)
    is Result.Err<V, E> ->
        Result.Err<V2, E>(error)
}

/** Transform error into a new result.
 *
 * Result.ok(42).flatMapError { Result.ok(100) } == Result.ok(42)
 * Result.err("failure").flatMapError { Result.ok(100) } == Result.ok(100)
 */
fun <V, E, E2> Result<V, E>.flatMapError(transform: (E) -> Result<V, E2>): Result<V, E2> = when (this) {
    is Result.Ok<V, E> ->
        Result.Ok<V, E2>(value)
    is Result.Err<V, E> ->
        transform(error)
}

sealed class Result <out V, out E> {
    // ABSTRACT

    /** Transform value.
     *
     * Result.ok(100).map { it + 1 } == Result.ok(101)
     * Result.err("failure").map { it + 1 } == Result.err("failure")
     */
    abstract fun <V2> map (transform: (V) -> V2): Result<V2, E>

    /** Transform error.
     *
     * Result.ok(100).mapError { it + "-mapped" } == Result.ok(100)
     * Result.err("failure").map { it + "-mapped" } == Result.err("failure-mapped")
     */
    abstract fun <E2> mapError (transform: (E) -> E2): Result<V, E2>

    /** Transform both sides.
     *
     * Result.ok(100).fold({ it + 1 }, { it + "-mapped" }) == Result.ok(101)
     * Result.err("failure").fold({ it + 1 }, { it + "-mapped" }) == Result.err("failure-mapped")
     */
    abstract fun <V2, E2> fold (transformValue: (V) -> V2, transformError: (E) -> E2): Result<V2, E2>

    // CONCRETE

    class Ok <V, E>(val value: V): Result<V, E>() {
        override fun toString() = "[Ok: $value]"
        override fun <V2> map(transform: (V) -> V2) = Ok<V2, E>(transform(value))
        override fun <E2> mapError(transform: (E) -> E2) = Ok<V, E2>(value)
        override fun <V2, E2> fold(transformValue: (V) -> V2, transformError: (E) -> E2) = Ok<V2, E2>(transformValue(value))
        override fun hashCode() = value?.hashCode() ?: 0
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Ok<*, *> && value == other.value
        }
    }

    class Err <V, E>(val error: E): Result<V, E>() {
        override fun toString() = "[Err: $error]"
        override fun <V2> map(transform: (V) -> V2) = Err<V2, E>(error)
        override fun <E2> mapError(transform: (E) -> E2) = Err<V, E2>(transform(error))
        override fun <V2, E2> fold(transformValue: (V) -> V2, transformError: (E) -> E2) = Err<V2, E2>(transformError(error))
        override fun hashCode() = error?.hashCode() ?: 0
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Err<*, *> && error == other.error
        }
    }

    companion object {
        // FACTORIES

        fun <V> ok (value: V): Result<V, Nothing> = Result.Ok(value)
        fun <E> err (error: E): Result<Nothing, E> = Result.Err(error)

        // MANY

        /** Combines results into a single result.
         *
         *  Result.all(ok(1), ok(2), ok(3)) == Result.ok([1, 2, 3])
         *  Result.all(ok(1), err("failure"), ok(3)) == Result.err("failure")
         */
        fun <V, E> all (vararg results: Result<V, E>): Result<List<V>, E> {
            return ok(results.map {
                when (it) {
                    is Ok<V, E> ->
                        it.value
                    is Err<V, E> ->
                        // Short-circuit
                        return err(it.error)
                }
            })
        }
    }
}



