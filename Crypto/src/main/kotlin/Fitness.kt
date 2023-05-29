import kotlin.math.ln

/**
 * Оценка текста на пригодность на основе эталонных частот n-грамм выбранного языка.
 * http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/
 */
interface Fitness {
    /**
     * Вычисляет значение фитнес-функции для заданного текста.
     * Ближе к 1 - лучше.
     */
    fun fitValue(text: CharSequence): Double
}

class NormLogFitness(private val n: Int, lang: String) : Fitness {

    private val nGrams = NGrams.load(n, lang)

    private val fitnessData = DoubleArray(countNGram(nGrams.alphabet, n))

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

    override fun fitValue(text: CharSequence) =
        encodedNGramsSeq(nGrams.alphabet, text, n)
            .map { fitnessData[it] }
            .average()
}
