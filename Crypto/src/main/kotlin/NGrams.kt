import java.io.File
import java.nio.DoubleBuffer
import kotlin.math.ln

fun prepareNGrams(n: Int, isRus: Boolean = true): Map<String, Int> {
    val prefix = if (isRus) "russian_" else "english_"
    val fileName = prefix + when (n) {
        1 -> "monograms.txt"
        2 -> "bigrams.txt"
        3 -> "trigrams.txt"
        4 -> "quadgrams.txt"
        else -> throw IllegalArgumentException("$n-grams not supported!")
    }
    val dataCount = File("RussianLetterFrequencies/$fileName")
        .readLines() //Charset.forName("Windows-1251")
        .associate { line ->
            line.split(" ")
                .let { it[0].lowercase() to it[1].toInt() }
        }
    return dataCount
}

fun searchNGrams(text: CharSequence, n: Int): Map<String, Double> {
    return text.windowed(n).groupingBy { it }.eachCount().normalize()
}

fun Map<String, Int>.normalize(): Map<String, Double> {
    val sum = values.sumOf { it.toLong() }
    return mapValues { it.value.toDouble() / sum }
}

fun normalizedNGramArr(n: Int, alphabet:  List<Char>, isRus: Boolean = true): DoubleArray {
    val nGrams = prepareNGrams(n, isRus) // не все
    println("nGrams size = ${nGrams.size}")
    //val alphabet = nGrams.keys.map { it.toCharArray()[0] }.distinct().sorted()

    val gSize = countNGram(alphabet, n)
    println("gSize = $gSize")
    val nGramFreq = DoubleArray(gSize)
    nGrams.forEach { (gram, count) ->
        nGramFreq[encodeNGram(alphabet, gram)] = count.toDouble()
    }

    //val (sum, positiveMin) = nGramFreq.sumToPositiveMin()
    val (sum, positiveMin) = nGramFreq.sum() to nGramFreq.filter { it > 0 }.minOf { it }
    val offset = ln(positiveMin / 10 / sum)

    var norm = 0.0
    for (i in 0 until gSize) {
        if (nGramFreq[i] > 0) {
            val prop = nGramFreq[i] / sum
            val new = ln(prop) - offset
            nGramFreq[i] = new
            norm += (prop * new)
        }
    }
    for (i in 0 until gSize) {
        nGramFreq[i] = nGramFreq[i] / norm
    }
    return nGramFreq
}

private operator fun DoubleBuffer.set(i: Int, value: Double) = put(i, value)


private fun DoubleBuffer.sumToPositiveMin(): Pair<Double, Double> {
    var sum = 0.0
    var min = Double.MAX_VALUE
    for (i in 0 until this.capacity()) {
        val local = get(i)
        sum += local
        if (local < min)
            min = local
    }
    return sum to min
}
