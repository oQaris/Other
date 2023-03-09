import java.io.File
import java.nio.charset.Charset

class Decoder(private val alphabet: List<Char>, private val origText: String, language: String = "russian") {
    //private val rnd = Random(43)
    private val ciphertext = prepareText(origText, alphabet)
    private var iteratedKeys = 0.0
    private val cash = createCash()
    private val fitness = NormLogFitness(4, language)

    fun breakCipher(maxRounds: Int = 1000, consolidate: Int = 3) {
        var localMax = Double.NEGATIVE_INFINITY
        var maxHit = 0
        var bestKey = alphabet.toList()

        //println("fitness, time, iter")
        for (round in 0..maxRounds) {
            val key = alphabet.shuffled()
            //measureTimeMillis {
            val (localBestKey, fitness) = hillClimbing(key)
            println("$fitness ")
            if (fitness > localMax) {
                localMax = fitness
                bestKey = localBestKey

                //val newText = decodeWith(ciphertext, bestKey)
                //println(newText)
            } else if (fitness == localMax) {
                maxHit++
                if (maxHit == consolidate)
                    break
            }
            //}.also { print(it) }
            //println(" $iteratedKeys")
            iteratedKeys = 0.0
        }

        val newText = decodeWith(origText, bestKey)
        println(newText)
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

    private fun hillClimbing(startKey: List<Char>): Pair<List<Char>, Double> {
        val keyLen = startKey.size
        val key = startKey.toMutableList()
        var plaintext = ciphertext.toString()
        var maxFitness = Double.NEGATIVE_INFINITY
        var wasUpdateKey = true

        while (wasUpdateKey) {
            wasUpdateKey = false
            for (idx1 in 0 until keyLen) {
                for (idx2 in idx1 + 1 until keyLen) {
                    val ch1 = key[idx1]
                    val ch2 = key[idx2]

                    key[idx1] = ch2
                    key[idx2] = ch1
                    val tmpText = decodeWith(ciphertext, key)
                    key[idx1] = ch1
                    key[idx2] = ch2

//                    val tmpText = StringBuilder(plaintext)
//                    for (idx in cash[alphabet.indexOf(ch1)]) {
//                        tmpText[idx] = ch2
//                    }
//                    for (idx in cash[alphabet.indexOf(ch2)]) {
//                        tmpText[idx] = ch1
//                    }

                    /*var tmpFitness = 0.0
                    encodedNGramsSeq(plaintext, 4).forEach { quadIdx ->
                        tmpFitness += quadgram[quadIdx]
                    }*/
                    val tmpFitness = fitness.fitValue(tmpText)
                    iteratedKeys++

                    if (tmpFitness > maxFitness) {
                        maxFitness = tmpFitness
                        plaintext = tmpText.toString()
                        wasUpdateKey = true
                        key[idx1] = ch2
                        key[idx2] = ch1
                    } /*else if (tmpFitness < maxFitness) {
                        for (idx in cash[alphabet.indexOf(ch1)]) {
                            plaintext[idx] = ch1
                        }
                        for (idx in cash[alphabet.indexOf(ch2)]) {
                            plaintext[idx] = ch2
                        }
                    }*/
                }
            }
        }
        return key to maxFitness
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

    // Вариант 2 - символы не из всего алфавита, английский текст
    val text2 = ("Rbo rpktigo vcrb bwucja wj kloj hcjd, km sktpqo, cq rbwr loklgo \n" +
            "vcgg cjqcqr kj skhcja wgkja wjd rpycja rk ltr rbcjaq cj cr.\n" +
            "-- Roppy Lpwrsborr").lowercase()

    val alphabet2 = ('a'..'z').toList()
    println("alpabet2: $alphabet2")

    Decoder(alphabet2, text2, "english").breakCipher()
    println()
}
