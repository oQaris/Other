import com.github.shiguruikai.combinatoricskt.combinations
import java.io.File
import java.nio.charset.Charset
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class Decoder(
    private val alphabet: List<Char>,
    private val origText: String,
    private val language: String = "russian"
) {
    private val ciphertext = origText.alphabetOnly(alphabet).joinToString("")
    private val cash = createCash()
    private val fitness = NormLogFitness(4, language)

    private fun printStats() {
        val stdNGrams = NGrams.load(1, language).normalized()
        val preparedText = origText.alphabetOnly(stdNGrams.alphabet).joinToString("")
        val textNGrams = NGrams.fromText(preparedText, 1).normalized()

        println("language: $language")
        println("alphabet: $alphabet")
        println("text length:           ${origText.length}")
        println("effective text length: ${ciphertext.length}")
        println("language entropy: ${stdNGrams.entropy()}")
        println("text entropy:     ${textNGrams.entropy()}")
    }

    @OptIn(ExperimentalTime::class)
    fun breakCipher(maxRounds: Int = 1000, consolidate: Int = 2) {
        var localMax = Double.NEGATIVE_INFINITY
        var maxHit = 0
        var bestKey = alphabet.toList()

        printStats()
        println("rounds: $maxRounds,  consolidate: $consolidate")
        println("fitness            time        iter")
        val populations = populationSequence().iterator()
        for (round in 0..maxRounds) {
            val key = populations.next()
            println("$round. $key")
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
        println("key: $bestKey")
        val newText = decodeWith(origText, bestKey)
        println("decrypted:\n$newText")
    }

    data class BreakerInfo(val key: List<Char>, val fitness: Double, val iterated: Int)

    private fun populationSequence() = sequence {
        // Сначала добавляем согласно частотному анализу символов
        val spaceAppender = if (alphabet.contains(' ')) listOf(' ') else listOf()
        val origFreq = spaceAppender + NGrams.load(1, language).data.keys.map { it.single() }
        val textFreq = NGrams.fromText(ciphertext, 1).data.keys.map { it.single() }
        val bijection = textFreq.zip(origFreq).toMap()
        yield(alphabet.map { bijection[it] ?: it })
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
        val key = startKey.toMutableList()
        val plaintext = StringBuilder(decodeWith(ciphertext, key))
        var maxFitness = fitness.fitValue(plaintext)
        var iteratedKeys = 0
        var wasUpdateKey = true

        while (wasUpdateKey) {
            wasUpdateKey = false
            key.indices.combinations(2).forEach { comb ->
                val idx1 = comb[0]
                val idx2 = comb[1]
                val ch1 = key[idx1]
                val ch2 = key[idx2]

//                    key[idx1] = ch2
//                    key[idx2] = ch1
//                    val tmpText = decodeWith(ciphertext, key)
//                    key[idx1] = ch1
//                    key[idx2] = ch2

                for (idx in cash[idx1]) {
                    plaintext[idx] = ch2
                }
                for (idx in cash[idx2]) {
                    plaintext[idx] = ch1
                }

                /*var tmpFitness = 0.0
                encodedNGramsSeq(plaintext, 4).forEach { quadIdx ->
                    tmpFitness += quadgram[quadIdx]
                }*/
                val tmpFitness = fitness.fitValue(plaintext)//tmpText
                iteratedKeys++

                if (tmpFitness > maxFitness) {
                    maxFitness = tmpFitness
                    wasUpdateKey = true
                    key[idx1] = ch2
                    key[idx2] = ch1
                    //}
                } else /*if (tmpFitness < maxFitness) */ {
                    for (idx in cash[idx1]) {
                        plaintext[idx] = ch1
                    }
                    for (idx in cash[idx2]) {
                        plaintext[idx] = ch2
                    }
                }
            }
        }
        //}
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

    Decoder(alphabet1, text1).breakCipher()
    println()

    // Вариант 2 - символы не из всего алфавита, английский текст
    val text2 = ("Rbo rpktigo vcrb bwucja wj kloj hcjd, km sktpqo, cq rbwr loklgo \n" +
            "vcgg cjqcqr kj skhcja wgkja wjd rpycja rk ltr rbcjaq cj cr.\n" +
            "-- Roppy Lpwrsborr").lowercase()

    val alphabet2 = ('a'..'z').toList()

    Decoder(alphabet2, text2, "english").breakCipher()
    println()
}
