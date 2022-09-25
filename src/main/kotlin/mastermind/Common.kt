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
}


fun <T> Iterable<T>.counting() = groupingBy { it }.eachCount().toMap()

@Serializable
data class ResultGuess(val guess: String, val isWin: Boolean)
