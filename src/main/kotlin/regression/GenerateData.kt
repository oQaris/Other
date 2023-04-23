package regression

import com.ezylang.evalex.Expression
import kotlin.math.*
import kotlin.random.Random

object GenerateData {
    var xOriginal = listOf<Double>()
    var yLearn = listOf<Double>()
    var xValue = listOf<Double>()
    var yValuePredicted = listOf<Double>()

    var funName = ""
    var polynomName = ""

    fun create(
        funStr: String,
        degree: Int,
        start: Double, end: Double, stepX: Double,
        noisePart: Double, maxErr: Double, isNormErr: Boolean,
    ) {
        require(start < end)
        funName = "y = $funStr"
        val exp = Expression(funStr)
        val function = { x: Double ->
            exp.with("x", x)
                .evaluate()
                .numberValue.toDouble()
        }

        xOriginal = (start..end step stepX).toList()
        yLearn = xOriginal.map(function).noise(noisePart, maxErr, isNormErr)

        val coefficients = leastSquaresMethod(xOriginal, yLearn, degree)
        polynomName = createPolynomName(coefficients)

        xValue = (start..end step (end - start) / 1000).toList()
        yValuePredicted = xValue.map { calculatePolynom(coefficients, it) }
    }
}

fun List<Double>.noise(changePercent: Double, maxErr: Double, norm: Boolean) = map {
    if (Random.nextDouble() < changePercent) {
        if (norm) {
            it + nextNorm() * maxErr
        } else {
            it + Random.nextDouble(-maxErr, maxErr)
        }
    } else it
}

// Метод Бокса-Мюллера генерации нормального распределения
fun nextNorm() = sqrt(-2 * ln(Random.nextDouble())) * cos(2 * PI * Random.nextDouble())

infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive, was: $step." }
    val sequence = generateSequence(start) { previous ->
        if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step
        if (next > endInclusive) null else next
    }
    return sequence.asIterable()
}

fun leastSquaresMethod(x: List<Double>, y: List<Double>, degree: Int): List<Double> {
    val xMatrix = Array(degree + 1) { DoubleArray(degree + 1) }
    for (i in 0..degree) {
        for (j in 0..degree) {
            xMatrix[i][j] = x.sumOf { xi -> xi.pow(i + j) }
        }
    }
    val yVector = DoubleArray(degree + 1) { i ->
        x.sumOf { xi -> xi.pow(i) * y[x.indexOf(xi)] }
    }
    return Matrix(xMatrix).solve(yVector).toList()
}

fun calculatePolynom(coef: List<Double>, x: Double): Double {
    return coef.mapIndexed { index, v ->
        v * x.pow(index.toDouble())
    }.sum()
}

fun createPolynomName(polynom: List<Double>): String {
    val builder = StringBuilder("y = ")
    polynom.asReversed().forEachIndexed { index, v ->
        if (abs(v) > 1e-5) {
            builder.append(String.format("%+.2f", v))
            if (index != polynom.size - 1) {
                builder.append("x^${polynom.size - 1 - index} ")
            }
        }
    }
    return builder.toString()
}