package mastermind

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class CommonKtTest {

    @ParameterizedTest
    @CsvSource(
        "1234, 5432, 1, 2",
        "5678, 5678, 4, 0",
        "9876, 1234, 0, 0",
        "1111, 2222, 0, 0",
        "1234, 4321, 0, 4",
        "1234, 1243, 2, 2",
        "123, 321, 1, 2",
        "124, 123, 2, 0",
        "12345, 54321, 1, 4",
        "9502, 2565, 1, 1",
        "9502, 1007, 1, 0",
    )
    fun evaluateTest(guess: Int, answer: Int, bulls: Int, cows: Int) {
        assertEquals(bulls * 10 + cows, evaluateFast(guess, answer))
        assertEquals(bulls bc cows, evaluate(guess.toString(), answer.toString()))
    }
}
