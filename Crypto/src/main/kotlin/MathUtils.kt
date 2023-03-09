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

fun <T : Number> Collection<T>.mean() = sumOf { it.toDouble() } / size
