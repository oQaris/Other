package seabattle

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BoardTest {
    private val board = Board()

    @Test
    fun addShipTest() {
        assertFalse(board.checkLoss())

        board.addShip(Ship(Cell(0, 0), 2, true))
        board.addShip(Ship(Cell(5, 4), 1, false))
        assertThrows(IllegalArgumentException::class.java) {
            board.addShip(Ship(Cell(9, 8), 3, true))
        }
        assertThrows(IllegalArgumentException::class.java) {
            board.addShip(Ship(Cell(1, 1), 2, false))
        }
    }

    @Test
    fun attackTest() {
        addShipTest()

        assertEquals(AnswerType.Hurt, board.attack(Cell(0, 0)))
        assertFalse(board.checkLoss())
        assertEquals(AnswerType.Past, board.attack(Cell(1, 0)))
        assertEquals(AnswerType.Killed, board.attack(Cell(0, 1)))
        assertFalse(board.checkLoss())
        assertEquals(AnswerType.Killed, board.attack(Cell(0, 0)))

        assertThrows(IllegalArgumentException::class.java) {
            board.attack(Cell(0, 10))
        }
        assertEquals(AnswerType.Killed, board.attack(Cell(5, 4)))
        assertTrue(board.checkLoss())
    }
}
