import kotlin.math.ln

enum class Language {
    RUS, ENG, DAN, FIN, FRA, DEU, ISL, POL, SPA, SWE
}

/**
 * Оценка текста на пригодность на основе эталонных частот n-грамм выбранного языка.
 * http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/
 */
interface Fitness {
    fun fitValue(text: CharSequence): Double
}

class EuclidWeightFitness(lang: String) : Fitness {

    private val weights = listOf(1.0 / 5, 13.0 / 60, 1.0 / 4, 1.0 / 3)

    private val probabilities = listOf(1, 2, 3, 4)
        .associateWith { n ->
            val nGrams = NGrams.load(n, lang)
            DoubleArray(countNGram(nGrams.alphabet, n)) {
                nGrams.data[decodeNGram(nGrams.alphabet, it, n)]!!
            }
        }

    /**
     * Используется взвешенная Евклидова метрика.
     * Сравнение с эталонным распределением частот по 1-4 граммам.
     * Ближе к 1 - лучше.
     */
    override fun fitValue(text: CharSequence): Double {
        var result = 0.0
        //todo
        /*probabilities.forEach { n, prob ->
            encodedNGramsSeq(alphabet, text, n).map { idx ->
                prob[idx]
            }.toList().mean()
            result+=
        }

        probabilities.forEachIndexed { i, gram ->
            result += delta(gram, searchNGrams(text, i + 1)) * weights[i]
        }*/
        return result
    }

    /*private fun delta(ng1: NGrams, ng2: NGrams): Double {
        return ng1.entries.sumOf { (gram, p) ->
            ((ng2[gram] ?: 0.0) - p).pow(2)
        }.let { sqrt(it) }
    }*/

    private fun normalize(input: Map<String, Double>): Map<String, Double> {
        val sum = input.values.sumOf { it }
        return input.mapValues { it.value / sum }
    }
}


class NormLogFitness(private val n: Int, lang: String) : Fitness {

    private val nGrams = NGrams.load(n, lang)

    val fitnessData = DoubleArray(countNGram(nGrams.alphabet, n))

    init {
        nGrams.data.forEach { (gram, count) ->
            fitnessData[encodeNGram(nGrams.alphabet, gram)] = count
        }

        val sum = fitnessData.sum()
        val positiveMin = fitnessData.asSequence()
            .filter { it > 0 }.minOf { it }
        val offset = ln(positiveMin / 10 / sum)

        var norm = 0.0
        for (i in fitnessData.indices) {
            if (fitnessData[i] > 0) {
                val prop = fitnessData[i] / sum
                val new = ln(prop) - offset
                fitnessData[i] = new
                norm += (prop * new)
            }
        }
        for (i in fitnessData.indices) {
            fitnessData[i] = fitnessData[i] / norm
        }
    }

    /**
     * Вычисляет значение фитнес-функции для заданного текста.
     * Ближе к 1 - лучше.
     */
    override fun fitValue(text: CharSequence) =
        encodedNGramsSeq(nGrams.alphabet, text, n).map { idx ->
            fitnessData[idx]
        }.toList().average()
}
