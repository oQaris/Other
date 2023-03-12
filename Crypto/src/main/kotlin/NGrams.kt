import java.io.File
import kotlin.math.log2

class NGrams(gramsAndCount: Map<String, Double>) {

    val data = gramsAndCount.toList().sortedByDescending { it.second }.toMap()

    val alphabet = data.keys.flatMapTo(mutableSetOf()) {
        it.toCharArray().asIterable()
    }.sorted()

    fun normalized(): NGrams {
        val sum = data.values.sum()
        return NGrams(data.mapValues { it.value / sum })
    }

    fun entropy(): Double {
        return -data.values.sumOf { it * log2(it) }
    }

    companion object {

        fun load(n: Int, lang: String): NGrams {
            val fileName = lang + "_" + when (n) {
                1 -> "monograms"
                2 -> "bigrams"
                3 -> "trigrams"
                4 -> "quadgrams"
                else -> throw IllegalArgumentException("$n-grams not supported!")
            } + ".txt"
            val dataCount = File("LetterFrequencies/$fileName")
                .readLines()
                .associate { line ->
                    val (gram, count) = line.split(" ").let { it[0] to it[1] }
                    require(gram.length == n)
                    gram.lowercase() to count.toDouble()
                }
            return NGrams(dataCount)
        }

        fun fromText(text: CharSequence, n: Int): NGrams {
            return NGrams(
                text.windowed(n)
                    .groupingBy { it }.eachCount()
                    .mapValues { it.value.toDouble() })
        }
    }
}

/**
 * Количество n-грамм для данного алфавита.
 */
fun countNGram(alphabet: List<Char>, n: Int) =
    alphabet.size.pow(n)

/**
 * Формирует n-граммы и кодирует их в K-ричную систему, где K - размер алфавита,
 * затем преобразует их в 10-ичную систему, возвращая последовательность (для оптимизации).
 */
fun encodedNGramsSeq(alphabet: List<Char>, text: CharSequence, n: Int) =
    text.alphabetOnly(alphabet).windowed(n) { gram ->
        encodeNGram(alphabet, gram)
    }

fun encodeNGram(alphabet: List<Char>, strGram: CharSequence): Int =
    strGram.map { char ->
        alphabet.indexOf(char)
            .also { require(it >= 0) }
    }.toDecimalSystem(alphabet.size)

fun encodeNGram(alphabet: List<Char>, strGram: List<Char>): Int =
    strGram.map { char ->
        alphabet.indexOf(char)
    }.toDecimalSystem(alphabet.size)

fun decodeNGram(alphabet: List<Char>, codeGram: Int, n: Int): String {
    return codeGram.fromDecimalSystem(alphabet.size)
        .map { alphabet[it] }
        .joinToString("")
        .padStart(n, alphabet[0])
}

fun CharSequence.alphabetOnly(alphabet: List<Char>) =
    this.asSequence()
        .map { it.lowercaseChar() }
        .filter { it in alphabet }

fun extractAlphabet(text: CharSequence): List<Char> {
    return text.toSet().filter { it.isLetter() }.sorted()
}
