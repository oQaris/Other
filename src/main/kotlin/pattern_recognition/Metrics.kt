package pattern_recognition

import kotlin.math.pow
import kotlin.math.sqrt

typealias Data = List<Int>
typealias Clazz = Int
typealias Metric = (Data, Data) -> Double

data class Item(val data: Data, val clazz: Clazz)

infix fun Data.itm(that: Clazz) = Item(this, that)


fun euclidean(v1: Data, v2: Data) =
    v1.zip(v2).sumOf {
        (it.first - it.second).toDouble().pow(2)
    }.let { sqrt(it) }