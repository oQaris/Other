package mastermind

import kotlinx.serialization.Serializable

data class BullsAndCows(val bulls: Int, val cows: Int) {
    override fun toString() = "($bulls, $cows)"
}

infix fun Int.bc(cows: Int) = BullsAndCows(this, cows)


fun <T> Iterable<T>.counting() = groupingBy { it }.eachCount().toMap()

@Serializable
data class ResultGuess(val guess: String, val isWin: Boolean)
