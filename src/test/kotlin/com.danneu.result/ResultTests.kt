package com.danneu.result

import org.junit.Assert.*
import org.junit.Test

class ResultTests {
    @Test
    fun testCreateValue() {
        val result = Result.ok(42)
        assertTrue("result is Ok type", result is Result.Ok)
    }

    @Test
    fun testCreateError() {
        val result = Result.err("failure")
        assertTrue("result is Err type", result is Result.Err)
    }

    @Test
    fun testGetOrElse() {
        assertEquals(100, Result.ok(100).getOrElse(-1))
        assertEquals(-1, Result.err("failure").getOrElse(-1))
    }

    @Test
    fun testFold() {
        assertEquals(Result.Ok<Int, String>(101), Result.ok(100).fold({ it + 1 }, { msg: String -> msg + "-mapped" }))
        assertEquals(Result.Err<Int, String>("failure-mapped"), Result.err("failure").fold({ n: Int -> n + 1 }, { it + "-mapped" }))
    }

    @Test
    fun testMap() {
        assertEquals(
            Result.Ok<Int, String>(3),
            Result.ok(1).map { it + 1 }.map { it + 1 }
        )
        assertEquals(
            Result.Err<Int, String>("failure"),
            Result.err("failure").map { n: Int -> n + 1 }.map { n: Int -> n + 1 }
        )
    }

    @Test
    fun testFlatMap() {
        assertEquals(
            "flatMap transitions ok result",
            Result.Ok<Int, String>(3),
            Result.ok(1).flatMap { Result.ok(2) }.flatMap { Result.ok(3) }
        )
        assertEquals(
            "err result stops flatMap",
            Result.Err<Int, String>("failure"),
            Result.ok(1).flatMap { Result.err("failure") }.flatMap { Result.ok(3) }
        )
    }

    @Test
    fun testFlatMapError() {
        assertEquals(
            "ok result stops flatMapError",
            Result.Ok<Int, String>(1),
            Result.ok(1).flatMapError { Result.ok(2) }
        )
        assertEquals(
            Result.Ok<Int, String>(2),
            Result.err("failure").flatMapError { Result.ok(2) }
        )
    }

    @Test
    fun testMapError() {
        assertEquals(
            "mapError cannot transition ok result",
            Result.Ok<Int, String>(1),
            Result.ok(1).mapError { "failure" }
        )
        assertEquals(
            "mapError transforms err",
            Result.Err<Int, String>("failure-mapped"),
            Result.err("failure").mapError { it + "-mapped" }
        )
    }
}
