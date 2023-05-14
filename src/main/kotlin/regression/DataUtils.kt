package regression

import kotlin.math.*
import kotlin.random.Random

// Генерация входных данных

fun generateByStep(range: ClosedRange<Double>, step: Double): DoubleArray {
    return (range step step).toList().toDoubleArray()
}

fun generateByCount(range: ClosedRange<Double>, count: Int): DoubleArray {
    return Array(count) { Random.nextDouble(range.start, range.endInclusive) }.toDoubleArray()
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

// Синглтон-объект для передачи параметров в Charts
object ChartParams {
    fun fill(
        xInput: DoubleArray,
        yInput: DoubleArray,
        funcStr: String,
        xOutput: DoubleArray,
        yOutput: DoubleArray,
        polyName: String
    ) {
        funName = funcStr
        xOriginal = xInput
        yLearn = yInput

        polynomName = polyName
        xValue = xOutput
        yValuePredicted = yOutput
    }

    var funName = ""
    var xOriginal = doubleArrayOf()
    var yLearn = doubleArrayOf()

    var polynomName = ""
    var xValue = doubleArrayOf()
    var yValuePredicted = doubleArrayOf()
}

fun Double.noise(noisePart: Double, maxErr: Double, isNormErr: Boolean) = run {
    if (Random.nextDouble() < noisePart) {
        if (isNormErr) {
            this + nextNorm() * maxErr
        } else {
            this + Random.nextDouble(-maxErr, maxErr)
        }
    } else this
}

// Метод Бокса-Мюллера генерации нормального распределения
fun nextNorm() = sqrt(-2 * ln(Random.nextDouble())) * cos(2 * PI * Random.nextDouble())

fun createPolynomName(polynom: DoubleArray): String {
    val builder = StringBuilder("y = ")
    polynom.reversed().forEachIndexed { index, v ->
        if (abs(v) > 1e-5) {
            builder.append(String.format("%+.2f", v))
            if (index != polynom.size - 1) {
                builder.append("x^${polynom.size - 1 - index} ")
            }
        }
    }
    return builder.toString()
}
