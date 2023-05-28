package perceptron

import kotlin.math.abs
import kotlin.math.exp
import kotlin.random.Random

class Perceptron(private val inputSize: Int, private val hiddenSize: Int, private val outputSize: Int) {
    private val hiddenLayerWeights = Array(hiddenSize) { DoubleArray(inputSize + 1) }
    private val outputLayerWeights = Array(outputSize) { DoubleArray(hiddenSize + 1) }

    init {
        // Задаем начальные веса случайным образом
        initializeWeights(hiddenLayerWeights)
        initializeWeights(outputLayerWeights)
    }

    fun train(inputs: DoubleArray, targets: DoubleArray, learningRate: Double): Double {
        val (hiddenLayerOutputs, outputLayerOutputs) = forwardPass(inputs)

        // Обратное распространение ошибки и коррекция весов
        var sumErr = 0.0
        val outputLayerErrors = DoubleArray(outputSize)
        for (i in 0 until outputSize) {
            val error = targets[i] - outputLayerOutputs[i]
            sumErr += abs(error)
            outputLayerErrors[i] = error * sigmoidDerivative(outputLayerOutputs[i])
            for (j in 0 until hiddenSize) {
                outputLayerWeights[i][j] += learningRate * outputLayerErrors[i] * hiddenLayerOutputs[j]
            }
            outputLayerWeights[i][hiddenSize] += learningRate * outputLayerErrors[i] // bias term
        }

        val hiddenLayerErrors = DoubleArray(hiddenSize)
        for (i in 0 until hiddenSize) {
            var errorSum = 0.0
            for (j in 0 until outputSize) {
                errorSum += outputLayerErrors[j] * outputLayerWeights[j][i]
            }
            hiddenLayerErrors[i] = errorSum * sigmoidDerivative(hiddenLayerOutputs[i])
            for (j in 0 until inputSize) {
                hiddenLayerWeights[i][j] += learningRate * hiddenLayerErrors[i] * inputs[j]
            }
            hiddenLayerWeights[i][inputSize] += learningRate * hiddenLayerErrors[i] // bias term
        }
        return sumErr / outputSize
    }

    fun predict(inputs: DoubleArray): DoubleArray {
        return forwardPass(inputs).second
    }

    private fun forwardPass(inputs: DoubleArray): Pair<DoubleArray, DoubleArray> {
        val hiddenLayerOutputs = DoubleArray(hiddenSize + 1)
        val outputLayerOutputs = DoubleArray(outputSize)

        // Прямое распространение
        for (i in 0 until hiddenSize) {
            var sum = hiddenLayerWeights[i][inputSize] // bias term
            for (j in 0 until inputSize) {
                sum += inputs[j] * hiddenLayerWeights[i][j]
            }
            hiddenLayerOutputs[i] = sigmoid(sum)
        }
        hiddenLayerOutputs[hiddenSize] = 1.0 // bias for the output layer

        for (i in 0 until outputSize) {
            var sum = outputLayerWeights[i][hiddenSize] // bias term
            for (j in 0 until hiddenSize) {
                sum += hiddenLayerOutputs[j] * outputLayerWeights[i][j]
            }
            outputLayerOutputs[i] = sigmoid(sum)
        }
        return hiddenLayerOutputs to outputLayerOutputs
    }

    private fun sigmoid(x: Double): Double {
        return 1.0 / (1.0 + exp(-x))
    }

    private fun sigmoidDerivative(y: Double): Double {
        return y * (1.0 - y)
    }

    private fun initializeWeights(weights: Array<DoubleArray>) {
        val random = Random(42L)
        for (i in weights.indices) {
            for (j in weights[i].indices) {
                weights[i][j] = random.nextDouble(-0.5, 0.5)
            }
        }
    }
}
