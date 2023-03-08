import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.random.Random
import kotlin.system.measureTimeMillis

internal class DecoderTest {

    @Test
    fun powBenchmark() {
        val n = 999999
        val from = 10
        val data1 = Array(n) { Random.nextInt(0, from) }
        val data2 = Array(n) { Random.nextInt(0, from) }

        val res1 = mutableListOf<Int>()
        measureTimeMillis {
            data1.zip(data2).forEach { (a, e) ->
                res1 += a.toDouble().pow(e.toDouble()).toInt()
            }
        }.also { println("stdlib: $it") }

        val res2 = mutableListOf<Int>()
        measureTimeMillis {
            data1.zip(data2).forEach { (a, e) ->
                res2 += a.pow(e)
            }
        }.also { println("custom: $it") }

        Assertions.assertEquals(0.0.pow(0.0).toInt(), 0.pow(0))
        Assertions.assertEquals(res1, res2)
    }

    @Test
    fun toDecimalSystemTest() {
        Assertions.assertEquals(
            721,
            listOf(1, 0, 1, 1, 0, 1, 0, 0, 0, 1).toDecimalSystem(2)
        )
        Assertions.assertEquals(
            63907,
            listOf(15, 9, 10, 3).toDecimalSystem(16) // F9A3
        )
        Assertions.assertEquals(
            0,
            listOf<Int>().toDecimalSystem(5)
        )
        Assertions.assertEquals(
            0,
            listOf(0).toDecimalSystem(5)
        )
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            listOf(9, 10, 8, 7).toDecimalSystem(10)
        }
    }

    @Test
    fun fromDecimalSystemTest() {
        Assertions.assertEquals(
            721.fromDecimalSystem(2),
            listOf(1, 0, 1, 1, 0, 1, 0, 0, 0, 1)
        )
        Assertions.assertEquals(
            63907.fromDecimalSystem(16),
            listOf(15, 9, 10, 3)
        )
        Assertions.assertEquals(
            0.fromDecimalSystem(5),
            listOf(0)
        )
    }

    @Test
    fun encodedNGramTest() {
        val alph = ('a'..'z').toList()
        val n = 3
        val encGramMap = alph.permutationsWithRepetition(n)
            .map { it.joinToString("") }
            .associateWith { encodeNGram(alph, it) }

        val maxGram = countNGram(alph, n)
        Assertions.assertEquals(maxGram, encGramMap.size)
        Assertions.assertEquals(maxGram, encGramMap.values.distinct().size)
        Assertions.assertTrue(maxGram < Int.MAX_VALUE)
    }

    @Test
    fun encodedNGramTest2() {
        val alph = ('a'..'z').toList()
        val enc = encodeNGram(alph, "tion")
        val dec = decodeNGram(alph, enc, 4)
        Assertions.assertEquals("tion", dec)
    }
}
