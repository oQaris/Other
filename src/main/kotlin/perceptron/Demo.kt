package perceptron

import kotlin.math.cos
import kotlin.random.Random

fun main() {
    //regressionDemo()
    classificationDemo()
}


fun regressionDemo() {
    // Создаем экземпляр перцептрона
    val mlp = Perceptron(
        inputSize = 1,
        hiddenSize = 15,
        outputSize = 1
    )

    // Задаем параметры обучения
    val learningRate = 0.1
    val numEpochs = 1_000

    // Генерируем случайные данные для обучения
    val random = Random(42L)
    val pi2 = 2 * Math.PI
    val numPoints = 100_000
    val inputs = Array(numPoints) { DoubleArray(1) { random.nextDouble(-pi2, pi2) } }
    val targets = Array(numPoints) { idx -> DoubleArray(1) { (cos(inputs[idx][0]) + 1) / 2 } }

    // Обучаем модель
    for (epoch in 0 until numEpochs) {
        var avgErr = 0.0
        for (i in inputs.indices) {
            avgErr += mlp.train(inputs[i], targets[i], learningRate)
        }
        println("epoch $epoch: err = ${avgErr / inputs.size}")
    }

    // Проверяем работу модели
    println("Predicted value for cos(0): ${mlp.predict(DoubleArray(1) { 0.0 })[0] * 2 - 1}")
    println("Predicted value for cos(pi): ${mlp.predict(DoubleArray(1) { Math.PI })[0] * 2 - 1}")
    println("Predicted value for cos(-pi/2): ${mlp.predict(DoubleArray(1) { -Math.PI / 2 })[0] * 2 - 1}")
}


fun classificationDemo() {
    val (x, y) = loadIrisDataset()
    val (train, test) = trainTestSplit(x, y, 0.20)

    val mlp = Perceptron(inputSize = 4, hiddenSize = 15, outputSize = 3)

    for (epoch in 0 until 50) {
        var error = 0.0
        train.forEach { (inputs, targets) ->
            error += mlp.train(inputs, targets, 0.05)
        }
        //accuracy(train, mlp)
        //accuracy(test, mlp)
        println("epoch $epoch: error = ${error / train.size}")
    }
    accuracy(test, mlp)
}

fun accuracy(dataset: List<Pair<DoubleArray, DoubleArray>>, mlp: Perceptron) {
    var correctCount = 0
    dataset.forEach { (inputs, actual) ->
        val predicted = mlp.predict(inputs)
        if (predicted.indices.maxByOrNull { predicted[it] } == actual.indices.maxByOrNull { actual[it] }) {
            correctCount++
        }
    }
    println("Accuracy: $correctCount/${dataset.size} (${correctCount.toDouble() / dataset.size})")
}

fun trainTestSplit(x: List<DoubleArray>, y: List<DoubleArray>, testRatio: Double):
        Pair<List<Pair<DoubleArray, DoubleArray>>, List<Pair<DoubleArray, DoubleArray>>> {
    val dataset = x.zip(y).shuffled()
    val trainSize = ((1 - testRatio) * dataset.size).toInt()
    val trainData = dataset.subList(0, trainSize)
    val testData = dataset.subList(trainSize, dataset.size)
    return trainData to testData
}

fun loadIrisDataset(): Pair<List<DoubleArray>, List<DoubleArray>> {
    val lines = Thread.currentThread().contextClassLoader.getResourceAsStream("iris.data").use { stream ->
        stream.bufferedReader().readLines().filter { it.isNotBlank() }
    }

    val x = mutableListOf<DoubleArray>()
    val y = mutableListOf<DoubleArray>()

    for (line in lines) {
        val parts = line.split(",")

        val features = DoubleArray(4) { i -> parts[i].toDouble() }
        val label = when (parts[4]) {
            "Iris-setosa" -> doubleArrayOf(1.0, 0.0, 0.0)
            "Iris-versicolor" -> doubleArrayOf(0.0, 1.0, 0.0)
            "Iris-virginica" -> doubleArrayOf(0.0, 0.0, 1.0)
            else -> throw IllegalArgumentException("Invalid label")
        }
        x.add(features)
        y.add(label)
    }
    return x to y
}
