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
    var isUnknownOrDuplicateError = false
    val known = listOf<String>(
        "Подсоединение по адресу 65.21.41.12:27953 отклонено",
        "Not setted Strategy Params",
        "Указан неподдерживаемый генератор NLMewTestBot1",
        "Failed to build group profile last time, don't repeat again FAuto.[HUSNGBot4].10.SPIN.STT_V3",
        "ParseLimitFile",
        "Error loading strategy parameters",
        "SocketTimeoutException: Connect timed out",
        "FileNotFoundException: \\\\\\\\10.10.10.200\\\\UpdateServer\\\\GlobalData\\\\Limits",
        "something wrong"
        /*"Not setted Strategy",
        "Connection refused",
        "Can't find games in BD",
        "Skip task",
        "Connect timed out",
        "Unknown player trying to post blind",
        "IllegalStateException: Undefined game type",
        "Need check bot PLOVBot2",
        "FileNotFoundException: \\\\\\\\10.10.10.200",
        "NullPointerException: null",
        "ArrayIndexOutOfBoundsException: null",
        "ArrayIndexOutOfBoundsException: Index",
        "GameException: Unknown currency code"*/
    )
    val frequency = IntArray(known.size)
    File("C:\\Users\\Oqaris\\Downloads\\total-json-07-09")
        .listFiles()?.forEach { file ->
            file.bufferedReader().lineSequence()
                .filter { it.contains("\"level\":\"ERROR\",") /*&& it.contains("PLO 6max PLOBot8") && it.contains("Can't find games in BD")*/ }
                //.filter { line -> known.any { line.contains(it) } }
                .forEach { line ->
                    var counter = 0
                    known.forEachIndexed { index, err ->
                        if (line.contains(err)) {
                            frequency[index]++
                            counter++
                        }
                    }
                    if (counter != 1) {
                        if (counter > 1) print("+ ") else print("?")
                        println(line)
                        isUnknownOrDuplicateError = true
                    }
                }
        }

    val sum = frequency.sum()
    println("Общее число ошибок: $sum")
    frequency.withIndex().sortedByDescending { it.value }.forEach {
        println("${known[it.index]}:\t\t\t${it.value.toDouble() / sum * 100} %")
    }
    require(!isUnknownOrDuplicateError) { "Присутствуют неизвестные или дублирующиеся ошибки!" }
}
