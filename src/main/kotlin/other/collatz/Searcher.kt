package other.collatz

val allNum = mutableListOf<Int>()
val allPvt = mutableMapOf<Int, Int>()

fun main() {
    val max = 1000
    val arrFreq = IntArray(max + 1)
    for (i in (1..max)) {
        val sq = toSequence(i)
        //arrFreq[i] = sq.size
        //allNum += sq

        var maxInc = 0
        var inc = 0
        var pvt = false
        sq.zipWithNext().forEach {
            if (it.first < it.second) {
                inc++
                if (inc > maxInc)
                    maxInc = inc
                pvt = false
            } else {
                if (pvt)
                    inc = 0
                pvt = true
            }
        }
        allPvt.putIfAbsent(maxInc, i)
    }

    /*(0..max).zip(arrFreq.toList())
        .forEach{ println(it) }*/

    //toSequence(27).forEach { println(it) }

    println(allPvt)
}

fun toSequence(num: Int) = generateSequence(num) {
    if (it % 2 == 0) it / 2
    else it * 3 + 1
}//.takeWhile { it != 1 }.plus(1).toList()
