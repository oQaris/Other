package pixfun

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun main() {
    val lin = chartMtx(1, 40) { 40.0 / it.toDouble() }
    println(lin.toStr())
}

fun chartMtx(start: Int, end: Int, func: (Int) -> Double): Array<Array<Boolean>> {
    val n = end - start
    val out = Array(n) { Array(n) { false } }

    var prevY: Int? = null
    for (x in start until end) {

        val y = n - 1 - func.invoke(x).roundToInt()
        if (y in 0 until n) {
            out[y][x - start] = true

            if (prevY != null) {
                val startY = min(prevY, y)
                val endY = max(prevY, y)
                for (extraY in startY until endY)
                    out[extraY][x - start] = true
            }
            prevY = y
        }
    }
    return out
}

fun Array<Array<Boolean>>.toStr() = joinToString("\n") { row ->
    row.joinToString("") { if (it) "0" else "-" }
}
