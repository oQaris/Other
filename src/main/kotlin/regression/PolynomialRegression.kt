package regression

import kotlin.math.pow

fun polynomialRegressionWithCV(
    x: DoubleArray,
    y: DoubleArray,
    degValues: IntArray,
    numFolds: Int,
    alphaValues: DoubleArray
): DoubleArray {
    val nSamples = x.size
    var bestDegree = 0
    var bestAlpha = 0.0
    var bestMSE = Double.POSITIVE_INFINITY

    for (alpha in alphaValues) {
        for (degree in degValues) {
            var mseSum = 0.0

            for (i in 0 until numFolds) {
                //todo рефакторинг с windowed()
                val start = i * nSamples / numFolds
                val end = (i + 1) * nSamples / numFolds

                val xTrain = x.sliceArray(0 until start) + x.sliceArray(end until nSamples)
                val yTrain = y.sliceArray(0 until start) + y.sliceArray(end until nSamples)
                val xTest = x.sliceArray(start until end)
                val yTest = y.sliceArray(start until end)

                val coefficients = ridgeRegression(xTrain, yTrain, degree, alpha)
                val yPred = xTest.map { calculatePolynom(coefficients, it) }

                val mse = meanSquaredError(yTest, yPred.toDoubleArray())
                mseSum += mse
            }

            val meanMSE = mseSum / numFolds

            if (meanMSE < bestMSE) {
                bestDegree = degree
                bestAlpha = alpha
                bestMSE = meanMSE
            }
        }
    }
    println("Best degree: $bestDegree")
    println("Best alpha: $bestAlpha")
    println("Best MSE: $bestMSE")

    return ridgeRegression(x, y, bestDegree, bestAlpha)
}

fun ridgeRegression(x: DoubleArray, y: DoubleArray, degree: Int, alpha: Double): DoubleArray {
    val m = degree + 1
    val xMatrix = Array(m) { i ->
        DoubleArray(m) { j ->
            x.sumOf { xi -> xi.pow(i + j) } + if (i == j) alpha else 0.0
        }
    }
    val yVector = DoubleArray(m) { i ->
        x.sumOf { xi -> xi.pow(i) * y[x.indexOfFirst { it == xi }] }
    }
    return Matrix(xMatrix).solve(yVector)
}

fun calculatePolynom(coef: DoubleArray, x: Double): Double {
    return coef.mapIndexed { index, v ->
        v * x.pow(index.toDouble())
    }.sum()
}

fun meanSquaredError(yTrue: DoubleArray, yPred: DoubleArray): Double {
    require(yTrue.size == yPred.size) { "Arrays must have the same size" }
    var sum = 0.0
    for (i in yTrue.indices) {
        sum += (yTrue[i] - yPred[i]) * (yTrue[i] - yPred[i])
    }
    return sum / yTrue.size
}
