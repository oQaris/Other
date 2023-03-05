import java.io.File
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round
import kotlin.system.measureTimeMillis

class Decoder(private val alphabet: List<Char>, text: String) {

    init {
        require(alphabet.size <= Byte.MAX_VALUE)
    }

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

    fun encodeNGrams(text: String, n: Int) {
        val iterator = encode(text).also {
            require(it.size >= n)
        }.iterator()

        val gSize = 7 // столько бит занимает положительная часть Byte
        var gIdx = 0
        var mask = 0
        repeat(n - 1) {
            // Накапливаем первое частичное значение граммы
            gIdx = (gIdx shl gSize) + iterator.nextByte()
            // Формируем маску, которая после всех итерация будет равна 2^n-1
            mask = (mask shl 1) + 1
        }
        val nGramFreq = DoubleArray(Byte.MAX_VALUE.toDouble().pow(n).toInt())
        for (byte in iterator) {
            // накладываем маску, чтобы удерживать грамму в окне размера n
            gIdx = ((gIdx and mask) shl gSize) + byte
            nGramFreq[gIdx]++
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

        var maxChars = ""
        var index = maxIdx
        repeat(n) {
            maxChars = alphabet[index and Byte.MAX_VALUE.toInt()] + maxChars
            index = index shr gSize
        }

        println(maxChars)
        println(sum)
        println(positiveMin)
        println(offset)
        println(norm)
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
    val text = File("C:\\Users\\oQaris\\Desktop\\en.txt").readText() //Charset.forName("Windows-1251")

    pFitness(prepareText(text, lang), IS_RUS).also { println(it) }
    Decoder(lang, text).encodeNGrams(text, 4)
}
