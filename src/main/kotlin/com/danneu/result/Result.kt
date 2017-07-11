package com.danneu.result

class UnwrapException(message: String) : Exception(message)

fun <V, E> Result<V, E>.getOrElse(default: V) = when (this) {
    is Result.Ok<V, E> ->
        value
    is Result.Err<V, E> ->
        default
}

fun <V, E> Result<V, E>.getOrElse(transformError: (E) -> V) = when (this) {
    is Result.Ok<V, E> ->
        value
    is Result.Err<V, E> ->
        transformError(error)
}

fun <V, V2, E> Result<V, E>.flatMap(transformValue: (V) -> Result<V2, E>): Result<V2, E> = when (this) {
    is Result.Ok<V, E> ->
        transformValue(value)
    is Result.Err<V, E> ->
        Result.Err<V2, E>(error)
}

fun <V, E, E2> Result<V, E>.flatMapError(transformError: (E) -> Result<V, E2>): Result<V, E2> = when (this) {
    is Result.Ok<V, E> ->
        Result.Ok<V, E2>(value)
    is Result.Err<V, E> ->
        transformError(error)
}

sealed class Result <out V, out E> {
    fun getOrThrow(): V = when (this) {
        is Ok ->
            value
        is Err ->
            throw UnwrapException("Cannot unwrap $this")
    }

    fun <V2> map(transformValue: (V) -> V2): Result<V2, E> = when (this) {
        is Ok ->
            Ok<V2, E>(transformValue(value))
        is Err ->
            Err<V2, E>(error)
    }

    fun <E2> mapError(transformError: (E) -> E2): Result<V, E2> = when (this) {
        is Ok ->
            Ok<V, E2>(value)
        is Err ->
            Err<V, E2>(transformError(error))
    }

    fun <V2> fold(transformValue: (V) -> V2, transformError: (E) -> V2): V2 = when (this) {
        is Ok ->
            transformValue(value)
        is Err ->
            transformError(error)
    }

    class Ok <out V, out E> internal constructor (val value: V): Result<V, E>() {
        override fun toString() = "Result.Ok($value)"
        override fun hashCode() = value?.hashCode() ?: 0
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Ok<*, *> && value == other.value
        }
    }

    class Err <out V, out E> internal constructor (val error: E): Result<V, E>() {
        override fun toString() = "Result.Err($error)"
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

        fun <V, E> all (vararg results: Result<V, E>) = all(results.asIterable())

        fun <V, E> all (results: Iterable<Result<V, E>>): Result<List<V>, E> {
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
