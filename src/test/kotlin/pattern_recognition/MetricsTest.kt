package pattern_recognition

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.sqrt

internal class MetricsTest {

    @Test
    fun identityTest() {
        val v1 = listOf(1, 2, 3, 4)
        val v2 = listOf(1, 2, 3, 4)

        assertEquals(manhattan(v1, v2), 0.0)
        assertEquals(euclidean(v1, v2), 0.0)
        assertEquals(chebyshev(v1, v2), 0.0)
        assertEquals(distance(v1, v2), 0.0)
        assertEquals(cosine(v1, v2), 0.0)
    }

    @Test
    fun sizingTest() {
        val v1 = listOf(1, 2, 3, 4)
        val v2 = listOf(2, 2, 2, 2)

        assertEquals(manhattan(v1, v2), 4.0)
        assertEquals(euclidean(v1, v2), sqrt(6.0))
        assertEquals(cosine(v1, v2), 0.0)
    }

    @Test
    fun cosineTest() {

    }
}