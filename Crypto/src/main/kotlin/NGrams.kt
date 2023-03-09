import java.io.File

fun prepareNGrams(n: Int, lang: String): Map<String, Double> {
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
            val (gram, freq) = line.split(" ").let { it[0] to it[1] }
            require(gram.length == n)
            gram.lowercase() to freq.toDouble()
        }
    return dataCount
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
    prepareText(text, alphabet).windowedSequence(n) { gram ->
        encodeNGram(alphabet, gram)
    }

fun encodeNGram(alphabet: List<Char>, strGram: CharSequence): Int =
    strGram.map { char ->
        alphabet.indexOf(char)
            .also { require(it >= 0) }
    }.toDecimalSystem(alphabet.size)

fun decodeNGram(alphabet: List<Char>, codeGram: Int, n: Int): String {
    return codeGram.fromDecimalSystem(alphabet.size)
        .map { alphabet[it] }
        .joinToString("")
        .padStart(n, alphabet[0])
}

fun prepareText(text: CharSequence, alphabet: List<Char>) =
    StringBuilder().apply {
        text.map { it.lowercaseChar() }
            .filter { it in alphabet }
            .forEach { append(it) }
    }

fun searchNGrams(text: CharSequence, n: Int): Map<String, Double> {
    return text.windowed(n).groupingBy { it }.eachCount().normalize()
}

private fun Map<String, Int>.normalize(): Map<String, Double> {
    val sum = values.sumOf { it.toLong() }
    return mapValues { it.value.toDouble() / sum }
}

fun extractAlphabet(text: CharSequence): List<Char> {
    return text.toSet().filter { it.isLetter() }.sorted()
}