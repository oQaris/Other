package regression

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.stage.Stage

fun main() {
    // https://ezylang.github.io/EvalEx/references/functions.html
    val function =
    //"x*sinr(x)"
    //"5x^3+x^2+5"
        //"cosr(x)"
        "log(x)"
    GenerateData.create(function, 6, 0.1, 15.0, 0.03, false)
    Application.launch(Charts::class.java)
}

class Charts : Application() {

    override fun start(primaryStage: Stage) {
        val lineChart = LineChart(NumberAxis(), NumberAxis())
        val original = Series<Number, Number>()
        val predicted = Series<Number, Number>()

        original.name = GenerateData.funName
        predicted.name = GenerateData.polynomName

        GenerateData.xOriginal.forEachIndexed { index, value ->
            original.data.add(XYChart.Data(value, GenerateData.yLearn[index]))
        }
        GenerateData.xValue.forEachIndexed { index, value ->
            predicted.data.add(XYChart.Data(value, GenerateData.yValuePredicted[index]))
        }
        lineChart.data.add(original)
        lineChart.data.add(predicted)
        lineChart.createSymbols = false

        primaryStage.scene = Scene(lineChart, 800.0, 600.0)
        primaryStage.show()
    }
}
