import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

typealias NGrams = Map<String, Double>

// ----------- Кеш ----------- //
// Русские
val rusData = listOf(
    prepareNGrams(1, true),
    prepareNGrams(2, true),
    prepareNGrams(3, true),
    prepareNGrams(4, true)
)
/*val monogramsRus = prepareNGrams(1, true)
val bigramsRus = prepareNGrams(2, true)
val trigramsRus = prepareNGrams(3, true)
val quadgramsRus = prepareNGrams(4, true)*/

// Английские
val engData = listOf(
    prepareNGrams(1, false),
    prepareNGrams(2, false),
    prepareNGrams(3, false),
    prepareNGrams(4, false)
)

// Логарифмическая Фитнес-функция

val fNormal = logFitness(engData[3])

fun logQuadgramFitness(text: CharSequence): Double {
    val localQuadgram = searchNGrams(text, 4)
    val f = logFitness(localQuadgram)
    return abs(f - fNormal) / fNormal
}

fun logFitness(nGram: NGrams): Double {
    return nGram.values.sumOf { log10(it) } / nGram.size
}

// Вероятностная Фитнес-функция

val weights = listOf(1.0 / 5, 13.0 / 60, 1.0 / 4, 1.0 / 3)

/**
 * Используется взвешеная Евклидова метрика.
 * Сравненние с эталонным распределением частот по 1-4 граммам.
 * Ближе к 0 - лучше.
 */
fun pFitness(text: CharSequence, isRus: Boolean): Double {
    val data = if (isRus) rusData else engData
    var result = 0.0
    data.forEachIndexed { i, gram ->
        result += delta(gram, searchNGrams(text, i + 1)) * weights[i]
    }
    return result
}

fun delta(ng1: NGrams, ng2: NGrams): Double {
    return ng1.entries.sumOf { (gram, p) ->
        ((ng2[gram] ?: 0.0) - p).pow(2)
    }.let { sqrt(it) }
}
