package pattern_recognition

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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
        assertEquals(chebyshev(v1, v2), 2.0)
        assertEquals(distance(v1, v2), 3.0)
        assertEquals(cosine(v1, v2), 0.420534335283965)
    }

    @Test
    fun cosineTest() {
        val orig = listOf(1, 1, 0)
        val e1 = listOf(9, 9, 0)
        val e2 = listOf(0, 1, 1)

        assertTrue(euclidean(orig, e1) > euclidean(orig, e2))
        assertTrue(cosine(orig, e1) < cosine(orig, e2))
        assertEquals(cosine(orig, e1), 0.0)
    }
}