import java.io.File
import java.nio.charset.Charset
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class Decoder(
    private val alphabet: List<Char>,
    private val origText: String,
    private val language: String = "russian"
) {
    private val ciphertext = prepareText(origText, alphabet)
    private val cash = createCash()
    private val fitness = NormLogFitness(4, language)

    private fun printParams(){

    }

    @OptIn(ExperimentalTime::class)
    fun breakCipher(maxRounds: Int = 1000, consolidate: Int = 3) {
        var localMax = Double.NEGATIVE_INFINITY
        var maxHit = 0
        var bestKey = alphabet.toList()

        println("fitness           time        iter")
        val populations = populationSequence().iterator()
        for (round in 0..maxRounds) {
            val key = populations.next()
            val (timedValue, time) = measureTimedValue {
                hillClimbing(key)
            }
            val (localBestKey, fitness, iteratedKeys) = timedValue
            println("$fitness $time $iteratedKeys")
            if (fitness > localMax) {
                localMax = fitness
                bestKey = localBestKey
            } else if (fitness == localMax) {
                if (maxHit++ == consolidate)
                    break
            }
        }
        println(bestKey)
        val newText = decodeWith(origText, bestKey)
        println(newText)
    }

    data class BreakerInfo(val key: List<Char>, val fitness: Double, val iterated: Int)

    //todo
    private fun populationSequence() = sequence<List<Char>> {
        while (true) {
            yield(alphabet.shuffled())
        }
    }

    /**
     * Данные о позициях каждого символа из алфавита в зашифрованном тексте.
     */
    private fun createCash(): Array<List<Int>> {
        return alphabet.map { char ->
            ciphertext.foldIndexed(listOf<Int>()) { idx, acc, c ->
                if (char == c) acc + idx else acc
            }
        }.toTypedArray()
    }

    private fun hillClimbing(startKey: List<Char>): BreakerInfo {
        val keyLen = startKey.size
        val key = startKey.toMutableList()
        val plaintext = StringBuilder(decodeWith(ciphertext, key))
        var maxFitness = Double.NEGATIVE_INFINITY
        var iteratedKeys = 0
        var wasUpdateKey = true

        while (wasUpdateKey) {
            wasUpdateKey = false
            //todo + 00
            for (idx1 in 0 until keyLen - 1) {
                for (idx2 in idx1 + 1 until keyLen) {
                    val ch1 = key[idx1]
                    val ch2 = key[idx2]

                    key[idx1] = ch2
                    key[idx2] = ch1
                    val tmpText = decodeWith(ciphertext, key)
                    key[idx1] = ch1
                    key[idx2] = ch2

//                    for (idx in cash[alphabet.indexOf(ch1)]) {
//                        plaintext[idx] = ch2
//                    }
//                    for (idx in cash[alphabet.indexOf(ch2)]) {
//                        plaintext[idx] = ch1
//                    }

                    /*var tmpFitness = 0.0
                    encodedNGramsSeq(plaintext, 4).forEach { quadIdx ->
                        tmpFitness += quadgram[quadIdx]
                    }*/
                    val tmpFitness = fitness.fitValue(tmpText)
                    iteratedKeys++

                    if (tmpFitness > maxFitness) {
                        maxFitness = tmpFitness
                        wasUpdateKey = true
                        key[idx1] = ch2
                        key[idx2] = ch1
                    }
//                    } else /*if (tmpFitness < maxFitness) */ {
//                        for (idx in cash[alphabet.indexOf(ch1)]) {
//                            plaintext[idx] = ch1
//                        }
//                        for (idx in cash[alphabet.indexOf(ch2)]) {
//                            plaintext[idx] = ch2
//                        }
//                    }
                }
            }
        }
        return BreakerInfo(key, maxFitness, iteratedKeys)
    }

    private fun decodeWith(text: CharSequence, key: List<Char>): String {
        val bijection = alphabet.zip(key).toMap()
        return text.map { bijection[it] ?: it }
            .joinToString("")
    }
}


fun main() {
    println("Heap max size: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB")

    // Вариант 1 - все буквы встречаются в тексте, замена включая пробел
    val text1 = File("08.txt").readText(Charset.forName("Windows-1251")).lowercase()

    val alphabet1 = extractAlphabet(text1) + ' '
    println("alpabet1: $alphabet1")

    Decoder(alphabet1, text1).breakCipher()
    println()
    return

    // Вариант 2 - символы не из всего алфавита, английский текст
    val text2 = ("Rbo rpktigo vcrb bwucja wj kloj hcjd, km sktpqo, cq rbwr loklgo \n" +
            "vcgg cjqcqr kj skhcja wgkja wjd rpycja rk ltr rbcjaq cj cr.\n" +
            "-- Roppy Lpwrsborr").lowercase()

    val alphabet2 = ('a'..'z').toList()
    println("alpabet2: $alphabet2")

    Decoder(alphabet2, text2, "english").breakCipher()
    println()
}
