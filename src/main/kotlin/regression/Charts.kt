package regression

import com.ezylang.evalex.Expression
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.stage.Stage

fun main() {
    // https://ezylang.github.io/EvalEx/references/functions.html
    val funcStr =
        // "x*sinr(x)"
        // "5x^3+x^2+5"
        // "cosr(x)"
        "log(x)"

    val exp = Expression(funcStr)
    val function = { x: Double ->
        exp.with("x", x)
            .evaluate()
            .numberValue.toDouble()
            .noise(noisePart = 0.8, maxErr = 0.1, isNormErr = false)
    }

    val rangeInput = 1.0..17.0
    val xInput = generateByCount(rangeInput, 20)
    val yInput = xInput.map(function).toDoubleArray()

    val stepOut = (rangeInput.endInclusive - rangeInput.start) / 1000
    val xOutput = generateByStep(rangeInput, stepOut)

    // Определяем степени полинома, которые мы хотим попробовать
    val degrees = intArrayOf(1, 2, 3, 4, 5, 6)
    // Определяем коэффициенты регуляризации alpha, которые мы хотим попробовать
    val alphas = doubleArrayOf(0.0, 0.01, 0.05, 0.1, 0.5)
    // Задаем количество фолдов для кросс-валидации
    val nFolds = 5

    // Вызываем функцию полиномиальной регрессии, чтобы получить наилучшую модель и оценить ее MSE
    val coefficients = polynomialRegressionWithCV(
        xInput,
        yInput,
        degrees,
        nFolds,
        alphas
    )
    val yOutput = xOutput.map { calculatePolynom(coefficients, it) }.toDoubleArray()
    val polyName = createPolynomName(coefficients)

    // Выводим графики
    ChartParams.fill(
        xInput, yInput, funcStr,
        xOutput, yOutput, polyName
    )
    Application.launch(Charts::class.java)
}

class Charts : Application() {

    override fun start(primaryStage: Stage) {
        val lineChart = LineChart(NumberAxis(), NumberAxis())
        val original = Series<Number, Number>()
        val predicted = Series<Number, Number>()

        original.name = ChartParams.funName
        predicted.name = ChartParams.polynomName

        ChartParams.xOriginal.forEachIndexed { index, value ->
            original.data.add(XYChart.Data(value, ChartParams.yLearn[index]))
        }
        ChartParams.xValue.forEachIndexed { index, value ->
            predicted.data.add(XYChart.Data(value, ChartParams.yValuePredicted[index]))
        }
        lineChart.data.add(original)
        lineChart.data.add(predicted)
        lineChart.createSymbols = false

        primaryStage.scene = Scene(lineChart, 800.0, 600.0)
        primaryStage.show()
    }
}
