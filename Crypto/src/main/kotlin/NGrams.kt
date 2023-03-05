import java.io.File

fun prepareNGrams(n: Int, isRus: Boolean = true): Map<String, Double> {
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
    return dataCount.normalize()
}

fun searchNGrams(text: CharSequence, n: Int): Map<String, Double> {
    return text.windowed(n).groupingBy { it }.eachCount().normalize()
}

private fun Map<String, Int>.normalize(): Map<String, Double> {
    val sum = values.sumOf { it.toLong() }
    return mapValues { it.value.toDouble() / sum }
}
