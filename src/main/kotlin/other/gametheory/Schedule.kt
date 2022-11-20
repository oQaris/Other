package other.gametheory

import kotlin.properties.Delegates

data class XY(val x: Int, val y: Int)
typealias XYTau = LinkedHashMap<XY, Int>

class Schedule(private val workTime: XYTau) {
    lateinit var tY: IntArray
    lateinit var tX: IntArray
    var n by Delegates.notNull<Int>()
    var result by Delegates.notNull<Int>()

    fun solve(): Schedule {
        n = workTime.keys.toSet().size
        tY = IntArray(n) { 0 }

        var flag = true
        while (flag) {
            flag = false
            workTime.entries.forEach {
                val newVal = tY[it.key.x] + it.value
                if (newVal > tY[it.key.y]) {
                    tY[it.key.y] = newVal
                    flag = true
                }
            }
        }
        result = tY.maxOrNull()!!

        tX = IntArray(n) { result }
        flag = true
        while (flag) {
            flag = false
            workTime.entries.forEach {
                val newVal = tX[it.key.y] - it.value
                if (newVal < tX[it.key.x]) {
                    tX[it.key.x] = newVal
                    flag = true
                }
            }
        }
        return this
    }

    fun genTable(): MutableList<List<Int>> {
        val table = mutableListOf<List<Int>>()
        workTime.entries.forEach {
            val tau = it.value
            val tR = tY[it.key.x]
            val tP = tX[it.key.y] - tau
            val crit = tP - tR
            table.add(listOf(tau, tR, tP, crit))
        }
        return table
    }
}
