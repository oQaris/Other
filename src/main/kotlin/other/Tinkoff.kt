package other.utils

import kotlin.math.abs
import kotlin.math.max

fun main() {
    task6()
}

fun task8() {
    val (n, m) = readln().split(" ").let { it[0].toInt() to it[1].toInt() }
    val domains = buildList {
        repeat(n) { add(readln()) }
    }
    repeat(m) {
        val (pre, post) = readln().split(" ").let { it[0] to it[1] }
        val countAvl = domains.count { it.startsWith(pre) && it.endsWith(post) }
        println(countAvl)
    }
}

fun task7() {
    val shop = readln()
    val q = readln().toInt()
    val segments = buildList {
        repeat(q) {
            add(readln().split(" ")
                .let { it[0].toInt() to it[1].toInt() })
        }
    }
    segments.forEach { seg ->
        val (start, end) = seg.first - 1 to seg.second - 1
        val sub = shop.substring(start, end + 1).toList().sorted()
        val idxs = listOf(start) + sub.map { shop.indexOf(it) }

        var shift = 0
        val distances = idxs.zipWithNext()
            .map { (f, s) ->
                f + shift to run {
                    if (f > s) shift += sub.size
                    s + shift
                }
            }
        println(distances.sumOf { abs(it.second - it.first) })
    }
}

fun task6() {
    val n = readln().toInt()
    val elevators = buildList {
        repeat(n) { add(readln().split(" ").let { it[0].toInt() to it[1].toInt() }) }
    }
    val allFloor = elevators.map { it.first }.toSet()
    val max = allFloor.maxOf { helper6task(elevators, it) }
    println(max)
}

fun helper6task(avlElvs: List<Pair<Int, Int>>, curFloor: Int): Int {
    val curEvls = avlElvs.filter { it.first == curFloor }
    return curEvls.maxOfOrNull {
        helper6task(avlElvs - it, it.second) + 1
    } ?: 0
}

fun task5() {
    val (n, k) = readln().split(" ").let { it[0].toInt() to it[1].toInt() }
    val names = buildList {
        repeat(n) { add(readln()) }
    }
    val sortedNames = names.sorted()
    repeat(k) {
        val (idx, prefix) = readln().split(" ").let { it[0].toInt() to it[1] }
        val name = sortedNames.filter { it.startsWith(prefix) }[idx - 1]
        println(names.indexOf(name) + 1)
    }
}

fun task4() {
    val variables = mutableMapOf<String, Int>()
    helper4task(variables)
}

fun helper4task(variables: MutableMap<String, Int>) {
    while (true) {
        val str = readln()

        if (str.contains("=")) {
            val vars = str.split("=")

            variables[vars[0]] = try {
                vars[1].toInt()

            } catch (e: NumberFormatException) {
                (variables[vars[1]] ?: 0)
                    .also { println(it) }
            }
        } else if (str == "{")
            helper4task(variables.toMutableMap())
        else return
    }
}

fun task3() {
    readln()
    val subscriptions = readln().split(" ").map { it.toInt() }

    val con = mutableListOf<Int>()
    val dis = mutableListOf<Int>()

    subscriptions.forEachIndexed { index, elem ->
        if (index % 2 == 0)
            con.add(elem)
        else dis.add(elem)
    }

    var profit = con.sum() - dis.sum()

    val (minC, maxD) = con.minOf { it } to dis.maxOf { it }
    if (minC < maxD)
        profit += (maxD - minC) * 2

    println(profit)
}

fun task2() {
    val num = readln().toInt()
    val winners = buildList {
        repeat(num) {
            add(readln().split(" ").toSet())
        }
    }
    val maxWins = winners.groupingBy { it }.eachCount().maxOf { it.value }
    println(maxWins)
}

fun task1() {
    fun readPlace() = readln().split(" ").map { it.toInt() }

    val firstPlace = readPlace()
    val secondPlace = readPlace()

    val all = (firstPlace + secondPlace)
    val x = mutableSetOf<Int>()
    val y = mutableSetOf<Int>()

    all.forEachIndexed { index, elem ->
        if (index % 2 == 0)
            x.add(elem)
        else y.add(elem)
    }

    val maxSide = max(x.maxOf { it } - x.minOf { it },
        y.maxOf { it } - y.minOf { it })

    println(maxSide * maxSide)
}
