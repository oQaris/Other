package regression

import com.ezylang.evalex.Expression
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt
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
        norm: Boolean,
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
        yLearn = xOriginal.map(function).noise(0.4, 0.5)

        val coefficients = leastSquaresMethod(xOriginal, yLearn, degree, norm)
        polynomName = createPolynomName(coefficients)

        xValue = (start..end step (end - start) / 1000).toList()
        yValuePredicted = xValue.map { calculatePolynom(coefficients, it) }
    }
}

fun List<Double>.noise(changePercent: Double, maxErr: Double) = map {
    if (Random.nextDouble() < changePercent) {
        val sign = if (Random.nextBoolean()) 1 else -1
        it + Random.nextDouble(maxErr) * sign
    } else it
}

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

fun leastSquaresMethod(x: List<Double>, y: List<Double>, degree: Int, norm: Boolean): List<Double> {
    val weight = generateWeight(y)
    val coefficients = Array(degree + 1) { 0.0 }
    for (i in 0..degree) {
        for (j in x.indices) {
            if (norm) {
                coefficients[i] += weight[j] * x[j].pow(i) * y[j]
            } else {
                coefficients[i] += x[j].pow(i) * y[j]
            }
        }
    }
    val xMatrix = Array(degree + 1) { DoubleArray(degree + 1) }
    for (i in 0..degree) {
        for (j in 0..degree) {
            xMatrix[i][j] = x.sumOf { xi -> xi.pow(i + j) }
        }
    }
    val yMatrix = DoubleArray(degree + 1) { i -> x.sumOf { xi -> xi.pow(i) * y[x.indexOf(xi)] } }
    val matrix = Matrix(xMatrix)
    val solution = matrix.solve(yMatrix)
    for (i in 0..degree) {
        coefficients[i] = solution[i]
    }
    return coefficients.toList()
}

fun generateWeight(y: List<Double>): List<Double> {
    // Определяем функцию весовых коэффициентов
    val n = y.size
    val mu = y.average()
    val sigma = y.sumOf { xi -> (xi - mu).pow(2) } / n
    val weights = y.map { xi ->
        val numerator = exp(-(xi - mu).pow(2) / (2 * sigma.pow(2)))
        val denominator = sqrt(2 * Math.PI * sigma.pow(2))
        numerator / denominator
    }
    return weights.toList()
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