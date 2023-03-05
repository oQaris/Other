package decoder

import com.github.shiguruikai.combinatoricskt.combinations
import com.github.shiguruikai.combinatoricskt.permutations
import inDictionary
import nGramm
import processors.sortedCounter
import tokens
import words
import java.io.File
import java.nio.charset.Charset
import kotlin.math.pow
import kotlin.math.sqrt

data class MorphStat(
    val frequency: Map<String, Double>,
    val bigram: Map<String, Double>,
    val trigram: Map<String, Double>
) {

    fun distance(other: MorphStat): Double {
        val (f1, f2) = aggregate(frequency, other.frequency)
        val (b1, b2) = aggregate(bigram, other.bigram)
        val (t1, t2) = aggregate(trigram, other.trigram)

        return euclidean(f1, f2) + euclidean(b1, b2) + euclidean(t1, t2)
    }

    private fun <K> aggregate(first: Map<K, Double>, second: Map<K, Double>): Pair<List<Double>, List<Double>> {
        return first.keys.intersect(second.keys)
            .associate { first[it]!! to second[it]!! }
            .entries.map { it.toPair() }.unzip()
    }

    private fun euclidean(v1: List<Double>, v2: List<Double>) =
        v1.zip(v2).sumOf {
            (it.first - it.second).pow(2)
        }.let { sqrt(it) }
}

class PermutationDecoder(val alphabet: List<Char>, encodedText: String) {
    private val text = encodedText.lowercase().trim()
    private val morphStat = calcMorphStat()
    private val globalFreq = morphStat.frequency.map { it.key.toCharArray()[0] }

    private fun calcMorphStat() = MorphStat(
        nGrammFreq(1),
        nGrammFreq(2),
        nGrammFreq(3)
    )

    private fun nGrammFreq(n: Int) = nGramm(text, n).run {
        val sum = values.sum().toDouble()
        mapValues { it.value / sum }
    }.toSortedMap()

    fun decode(): String {
        val localFreq = text.map { it.lowercaseChar() }
            .filter { it in globalFreq }
            .groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }
            .map { it.key }

        val bijection = localFreq.zip(globalFreq).toMap()

        return fastDecodeWith(text, bijection)
    }

    fun cycleDecode() {
        val localFreq = text.filter { it in globalFreq }
            .groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }
            .map { it.key }

        var counter = 0
        var maxNumDecWords = 0
        localFreq.permutations().forEach { permut ->
            val bijection = permut.zip(globalFreq).toMap()
            val decode = fastDecodeWith(text, bijection)
            val trueDecWords = decode.tokens().words()
                .filter { it.inDictionary() }.toSet()

            if (trueDecWords.size > maxNumDecWords) {
                maxNumDecWords = trueDecWords.size
                println(decode)
                println(trueDecWords)
                println(counter)
                println("============================================================================")
            }
            counter++
        }
    }

    fun crossDecode() {
        val localFreq = text.filter { it in globalFreq }
            .groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }
            .map { it.key }

        val popularVars = mutableListOf<Pair<Char, Char>>()
        text.tokens().words().forEach { encWord ->
            val allLocalVars = localFreq.combinations(encWord.toSet().size)
                .flatMap { it.permutations() }
                .map { encWord.toSet().zip(it) }
                .filter { fastDecodeWith(encWord, it.toMap()).inDictionary() }
                .flatten()
            popularVars += allLocalVars
            popularVars.sortedCounter().also { println(it) }
        }

        popularVars.sortedCounter().also { println(it) }
    }

    /**
     * Декодирует текст по однозначному соответствию с сохранением регистра.
     * @param bijection отображение из символов в нижнем регистре.
     */
    private fun decodeWith(txt: String, bijection: Map<Char, Char>) =
        txt.fold("") { acc, c ->
            acc + (bijection[c.lowercaseChar()]?.let {
                if (c.isUpperCase()) it.uppercaseChar() else it
            } ?: c)
        }

    private fun fastDecodeWith(txt: String, bijection: Map<Char, Char>) =
        StringBuilder(txt).apply {
            forEachIndexed { i, c ->
                setCharAt(i, bijection[c] ?: c)
            }
        }.toString()
}

fun main() {
    println(nGramm("мама мыла раму", 3))
    val enc = File("08.txt").readText(Charset.forName("Windows-1251"))
    val rusFreq = "оеаинтсрвлкмдпуяыьгзбчйхжшюцщэфъё"
    val big = mapOf(
        "СТ" to 0.1,
        "НО" to 0.1,
        "НЕ" to 0.1,
        "ЕН" to 0.1,
        "ТО" to 0.1,
        "НА" to 0.1,
        "ОВ" to 0.1,
        "НИ" to 0.1,
        "РА" to 0.1,
        "РО" to 0.1,
        "ВО" to 0.1,
        "КО" to 0.1,
        "ПО" to 0.1,
    )
    val trig = listOf(
        "СТО" to 0.1,
        "ЕНО" to 0.1,
        "НОВ" to 0.1,
        "ТОВ" to 0.1,
        "АТЬ" to 0.1,
        "ОВО" to 0.1,
        "ОВА" to 0.1,
        "ЧТО" to 0.1,
        "ТАК" to 0.1,
        "ЭТО" to 0.1,
        "ПРО" to 0.1,
        "КАК" to 0.1,
        "ТЕБ" to 0.1,
    )
    //Decoder(enc.lowercase()).decode()
    PermutationDecoder(rusFreq.toList(), enc).crossDecode()
}
