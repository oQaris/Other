import java.io.File
import kotlin.math.pow

fun countNGram(alphabet: List<Char>, n: Int) =
    alphabet.size.pow(n)

fun encodeNGram(alphabet: List<Char>, strGram: CharSequence): Int =
    strGram.map { alphabet.indexOf(it) }
        .toDecimalSystem(alphabet.size)

fun decodeNGram(alphabet: List<Char>, codeGram: Int, n: Int): String {
    return codeGram.fromDecimalSystem(alphabet.size)
        .map { alphabet[it] }
        .joinToString("")
        .padStart(n, alphabet[0])
}

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
