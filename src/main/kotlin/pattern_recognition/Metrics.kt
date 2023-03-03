package pattern_recognition

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

typealias Data = List<Int>
typealias Clazz = Int
typealias Metric = (Data, Data) -> Double

data class Item(val data: Data, val clazz: Clazz)

infix fun Data.itm(that: Clazz) = Item(this, that)

// Метрики Минковского

fun manhattan(v1: Data, v2: Data) =
    v1.zip(v2).sumOf {
        abs(it.first - it.second)
    }.toDouble()

fun manhattan(v1: Data, v2: Data) =
    v1.zip(v2).sumOf {
        abs(it.first - it.second)
    }.toDouble()

fun euclidean(v1: Data, v2: Data) =
    v1.zip(v2).sumOf {
        (it.first - it.second).toDouble().pow(2)
    }.let { sqrt(it) }

fun chebyshev(v1: Data, v2: Data) =
    v1.zip(v2).maxOf { abs(it.first - it.second) }.toDouble()

fun distance(v1: Data, v2: Data) =
    v1.zip(v2).count { it.first!=it.second }.toDouble()



fun cosine(v1: Data, v2: Data) = v1.zip(v2).run {
    acos(sumOf { it.first * it.second } /
            (sqrt(sumOf { it.first.toDouble().pow(2) })
                    * sqrt(sumOf { it.second.toDouble().pow(2) })))
}