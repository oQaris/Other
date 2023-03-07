import kotlin.math.ln
import kotlin.math.round
import kotlin.system.measureTimeMillis

class Decoder(private val alphabet: List<Char>, text: String) {

    private val ciphertext = prepareText(text, alphabet)
    private var iteratedKeys = 0.0

    fun breakCipher(maxRounds: Int = 10000, consolidate: Int = 3) {

        var localMin = Double.MAX_VALUE
        var bestKey: List<Char>
        println("fitness, time, iter")
        for (round in 0..maxRounds) {
            val key = alphabet.shuffled()
            measureTimeMillis {
                val (localBestKey, fitness) = hillClimbing(ciphertext, key)
                print("$fitness ")
                if (fitness < localMin) {
                    localMin = fitness
                    bestKey = localBestKey

                    val bijection = alphabet.zip(bestKey).toMap()
                    val newText = fastDecodeWith(ciphertext, bijection)
                    println(newText)
                }
            }.also { print(it / 1000) }
            println(" $iteratedKeys")
            iteratedKeys = 0.0
        }
    }

    private fun hillClimbing(ciphertext: CharSequence, startKey: List<Char>): Pair<List<Char>, Double> {
        val keyLen = startKey.size
        var bestKey = startKey
        var maxFitness = Double.MAX_VALUE
        var wasUpdateKey = true

        while (wasUpdateKey) {
            wasUpdateKey = false
            for (idx1 in 0 until keyLen) {
                for (idx2 in idx1 + 1 until keyLen) {
                    val ch1 = bestKey[idx1]
                    val ch2 = bestKey[idx2]

                    val key = bestKey.toMutableList().also {
                        it[idx1] = ch2
                        it[idx2] = ch1
                    }
                    iteratedKeys++
                    val bijection = alphabet.zip(key).toMap()
                    val newText = fastDecodeWith(ciphertext, bijection)

                    val fitness = pFitness(newText, IS_RUS)
                    if (fitness < maxFitness) {
                        bestKey = key
                        maxFitness = fitness
                        wasUpdateKey = true
                    }
                }
            }
        }
        return bestKey to maxFitness
    }

    private fun fastDecodeWith(txt: CharSequence, bijection: Map<Char, Char>) =
        StringBuilder(txt).apply {
            forEachIndexed { i, c ->
                setCharAt(i, bijection[c] ?: c)
            }
        }.toString()

    private fun encode(text: String): ByteArray {
        return text.map {
            alphabet.indexOf(
                it.lowercaseChar()
            ).toByte()
        }.filter { it > -1 }
            .toByteArray()
    }

    /**
     * Формирует n-граммы и кодирует их в K-ричную систему, где K - размер алфавита,
     * затем преобразует их в 10-ичную систему, возвращая последовательность (для оптимизации).
     */
    private fun encodedNGramsSeq(text: CharSequence, n: Int) =
        text.windowedSequence(n) { gram ->
            encodeNGram(alphabet, gram)
        }

    fun normalizeNGrams(text: String, n: Int) {
        val gSize = countNGram(alphabet, n)
        val nGramFreq = DoubleArray(gSize)
        encodedNGramsSeq(prepareText(text, alphabet), n).forEach {
            nGramFreq[it]++
        }

        val sum = nGramFreq.sum()
        val positiveMin = nGramFreq.filter { it > 0 }.minOf { it }
        val offset = ln(positiveMin / 10 / sum)

        var norm = 0.0
        var hhh = 0
        for (i in nGramFreq.indices) {
            if (nGramFreq[i] > 0) {
                hhh++
                val prop = nGramFreq[i] / sum
                val new = ln(prop) - offset
                nGramFreq[i] = new
                norm += (prop * new)
            }
        }
        for (i in nGramFreq.indices) {
            nGramFreq[i] = round(nGramFreq[i] / norm * 1000)
        }

        val maxVal = nGramFreq.maxOf { it }
        val maxIdx = nGramFreq.indexOfFirst { it == maxVal }

        val maxChars = decodeNGram(alphabet, maxIdx, n)

        println(maxChars)
        println(sum)
        println(positiveMin)
        println(offset)
        println(norm)
        println()

        nGramFreq.forEachIndexed { index, d ->
            if (d != 0.0)
                println("${decodeNGram(alphabet, index, n)} -> $d")
        }
    }
}

fun prepareText(text: String, alphabet: List<Char>) =
    StringBuilder().apply {
        text.lowercase()
            .filter { it in alphabet }
            .forEach { append(it) }
    }

val RUS_ALPHABET = rusData[0].keys.map { it.toCharArray()[0] }
val ENG_ALPHABET = engData[0].keys.map { it.toCharArray()[0] }

const val IS_RUS = false

fun main() {
    val lang = if (IS_RUS) RUS_ALPHABET else ENG_ALPHABET
    val text = "The Tower of London\n" +
            "In the year 1066, after his victory at the Battle of Hastings, William the Conqueror was seeking to strengthen his control over the subdued1 English territories. In the following 20 years in England nearly 40 castles were founded by him and his vassals. It was probably the largest castle-building operation in the whole history of medieval2 Europe.\n" +
            "One of the castles was to be founded inside London, already the largest English town in those times. The so-called Tower of London was built on remains of an ancient Roman fortification, and initially was built mainly from timber. Only a hundred years later it was reinforced with stone. The castle takes its name from the White Tower, which is the name of the main keep that still stands as of today. People from other towns referred to it as The Tower of London, and eventually it became a name widespread3 enough to stick.\n" +
            "Given its location and strategic importance, the castle soon became a residence for the richest and the most influential people across England. Over the years the castle has expanded greatly, because each of its owners was always seeking to add something distinct4 to its fortifications. One of the darker stories of that age is the tale of the Princes in the Tower, two young boys of royal blood who were declared illegitimate and then murdered by some unknown assailant5. Remains of two boys were found inside the castle in a wooden box in 1674.\n" +
            "Starting in the 16th century, the castle started to see its use as a royal residence. It gained much notoriety6 in following years though, as it was also used as a prison and a place of execution for people who’d fall out of favour with their rulers.\n" +
            "In modern times The Tower of London became less ominous7. At some point8 there was even a zoo inside. It started as a collection of royal pets that quickly outgrew its accommodations9 and was soon moved to the London Zoo located inside Regent’s Park. It’s still open nowadays and is a popular tourist landmark.\n" +
            "Since 1988 The Tower of London has been listed as a UNESCO World Heritage10 Site. In the 21st century it’s mainly a tourist attraction. Usually you can visit the castle from Wednesday to Sunday, from 10 AM to 6 PM. The entrance fee for an adult is 25£. Visitors have free Wi-Fi access and can also buy some signature snacks in one of the cafes or kiosks inside."

    pFitness(prepareText(text, lang), IS_RUS).also { println(it) }
    Decoder(lang, text).normalizeNGrams(text, 4)
}
