import java.io.File
import kotlin.math.pow

fun List<Int>.toDecimalSystem(k: Int): Int {
    return asReversed().foldIndexed(0) { index, acc, value ->
        require(value < k)
        acc + value * k.pow(index)
    }
}

fun Int.fromDecimalSystem(k: Int) = buildList {
    var tmp = this@fromDecimalSystem
    do {
        add(tmp % k)
        tmp /= k
    } while (tmp != 0)
}.asReversed()

@Suppress("NOTHING_TO_INLINE") // see DecoderTest.powBenchmark
inline fun Int.pow(exp: Int) = this.toDouble().pow(exp).toInt()


fun main() {
    val known = listOf(
        "Not setted Strategy Prams",
        "Connection refused",
        "Can't find games in BD",
        "Skip task",
        "Connect timed out",
        "Unknown player trying to post blind",
        "IllegalStateException: Undefined game type",
        "IllegalStateException: Need logic fix accId",
        "FileNotFoundException: \\\\\\\\10.10.10.200\\\\UpdateServer\\\\GlobalData\\\\Limits",
        "NullPointerException: null",
        "ArrayIndexOutOfBoundsException: null",
        "ArrayIndexOutOfBoundsException: Index",
        "GameException: Unknown currency code"
    )
    val frequency = IntArray(known.size)
    File("C:\\Users\\Oqaris\\Downloads\\total-json-log")
        .listFiles()?.forEach { file ->
            file.bufferedReader().lineSequence()
                .filter { it.contains("\"level\":\"ERROR\",") /*&& it.contains("PLO 6max PLOBot8") && it.contains("Can't find games in BD")*/ }
                .filter { line -> known.any { line.contains(it) } }
                .forEach { line ->
                    var counter = 0
                    known.forEach { err ->
                        if (line.contains(err)) {
                            frequency[known.indexOf(err)]++
                            counter++
                        }
                    }
                    if (counter != 1)
                        println(line)
                }
        }

    val sum = frequency.sum() / 100
    frequency.sortedArrayDescending().forEachIndexed { idx, fr ->
        println("${known[idx]}:\t\t\t${fr.toDouble() / sum} %")
    }
}

fun <T : Number> Collection<T>.mean() = sumOf { it.toDouble() } / size
