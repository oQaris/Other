package mastermind

import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition
import kotlinx.serialization.Serializable
import kotlin.math.min

data class BullsAndCows(val bulls: Int, val cows: Int) {
    override fun toString() = "($bulls, $cows)"
}

infix fun Int.bc(cows: Int) = BullsAndCows(this, cows)

/**
 * Последовательность всевозможных строк длины [len], содержащих числа из [range]
 */
fun answerSequence(len: Int, range: IntRange = 0..9): Sequence<String> {
    return range.permutationsWithRepetition(len).map { it.joinToString("") }
}

/**
 * Число быков и коров для текущей догадки и
 * evaluate(a, b) == evaluate(b, a)
 */
fun evaluate(guess: String, answer: String): BullsAndCows {
    val counterAnswer = answer.toList().counting()

    val matches = guess.toList().counting().entries
        .sumOf { (t, u) -> min(u, counterAnswer[t] ?: 0) }

    val bulls = guess.zip(answer).count { it.first == it.second }
    return bulls bc matches - bulls

    /*require(guess.length == answer.length) { "Guess and answer must have the same length" }

    val bulls = guess.zip(answer).count { (g, a) -> g == a }
    val guessCharCount = IntArray(10)
    val answerCharCount = IntArray(10)
    var cows = 0
    for (i in guess.indices) {
        val guessChar = guess[i]
        val answerChar = answer[i]
        if (guessChar == answerChar) {
            continue
        }
        if (guessCharCount[answerChar - '0'] > 0) {
            cows++
            guessCharCount[answerChar - '0']--
        } else {
            guessCharCount[guessChar - '0']++
        }
        if (answerCharCount[guessChar - '0'] > 0) {
            cows++
            answerCharCount[guessChar - '0']--
        } else {
            answerCharCount[answerChar - '0']++
        }
    }
    return BullsAndCows(bulls, cows)*/
}

fun evaluateFast(guess: Int, answer: Int, base: Int = 10): Int {
    var guessCopy = guess
    var answerCopy = answer
    var bulls = 0
    var cows = 0

    val guessCharCount = IntArray(base)
    val answerCharCount = IntArray(base)
    while (guessCopy > 0 && answerCopy > 0) {
        val guessDigit = guessCopy % base
        val answerDigit = answerCopy % base
        if (guessDigit == answerDigit) {
            bulls++
        } else {
            if (guessCharCount[answerDigit] > 0) {
                cows++
                guessCharCount[answerDigit]--
            } else {
                guessCharCount[guessDigit]++
            }
            if (answerCharCount[guessDigit] > 0) {
                cows++
                answerCharCount[guessDigit]--
            } else {
                answerCharCount[answerDigit]++
            }
        }
        guessCopy /= base
        answerCopy /= base
    }
    //require(guessCopy == 0 && answerCopy == 0) { "Guess and answer must have the same length" }
    return bulls * base + cows
}

fun <T> Iterable<T>.counting() = groupingBy { it }.eachCount().toMap()

@Serializable
data class ResultGuess(val guess: String, val isWin: Boolean)
