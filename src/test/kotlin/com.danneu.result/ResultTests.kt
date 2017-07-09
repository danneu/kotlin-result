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
        assertEquals(101, Result.ok(100).fold({ it + 1 }, { -1 }))
        assertEquals(-1, Result.err("failure").fold({ n: Int -> n + 1 }, { -1 }))
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

    @Test
    fun testAll() {
        assertEquals(
            "Result.all combines ok values",
            Result.Ok<List<Int>, String>(listOf(1, 2, 3)),
            Result.all(Result.ok(1), Result.ok(2), Result.ok(3))
        )

        assertEquals(
            "Result.all becomes first err",
            Result.Err<List<Int>, String>("a"),
            Result.all(
                Result.ok(1), Result.err("a"),
                Result.ok(2), Result.err("b"),
                Result.ok(3), Result.err("c")
            )
        )
    }

    @Test
    fun getOrThrowOk() {
        assertEquals("Result.getOrThrow() returns Result.Ok value", 42, Result.ok(42).getOrThrow())
    }

    @Test(expected = UnwrapException::class)
    fun getOrThrowErr() {
        Result.err(42).getOrThrow()
    }

    @Test
    fun testEquals() {
        assertEquals(Result.ok(42), Result.ok(42))
        assertEquals(Result.err("failure"), Result.err("failure"))
        assertEquals(Result.ok(null), Result.ok(null))
        assertEquals(Result.err(null), Result.err(null))
        assertNotEquals(Result.ok(null), Result.err(null))
        assertNotEquals(Result.ok(42), Result.err("failure"))
        assertNotEquals(Result.ok(42), Result.ok(-42))
        assertNotEquals(Result.err("failure A"), Result.err("failure B"))
    }
}
