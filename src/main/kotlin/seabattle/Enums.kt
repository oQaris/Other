package seabattle

data class Ship(val startCell: Cell, val len: Int, val isHorizontal: Boolean)

data class Settings(val boardSize: Int, val poolShips: List<Int>)

data class Cell(val row: Int, val col: Int)

fun parse(str: String): Cell {
    require(str.length == 2)
    val (row, col) = str.uppercase().let { it[0] to it[1] }
    return Cell(row - 'A', col.toString().toInt())
}

enum class CellType {
    EmptyHidden, ShipHidden, Empty, Ship;

    fun isHidden() = when (this) {
        EmptyHidden, ShipHidden -> true
        else -> false
    }

    fun unhidden() = when (this) {
        EmptyHidden -> Empty
        ShipHidden -> Ship
        else -> this
    }

    override fun toString() = when (this) {
        Empty -> "*"
        Ship -> "X"
        else -> "_"
    }
}

enum class AnswerType {
    /** Мимо */
    Past,

    /** Ранил */
    Hurt,

    /** Убил */
    Killed,

    /** Конец игры */
    EndGame
}

enum class GameState {
    /** Ходит первый */
    FirstMove,

    /** Ходит второй */
    SecondMove,

    /** Первый выиграл */
    FirstWin,

    /** Второй выиграл */
    SecondWin;

    fun nextStep() = when (this) {
        FirstMove -> SecondMove
        SecondMove -> FirstMove
        else -> throw UnsupportedOperationException()
    }

    fun toWin() = when (this) {
        FirstMove -> FirstWin
        SecondMove -> SecondWin
        else -> throw UnsupportedOperationException()
    }

    fun inGame() = when (this) {
        FirstMove, SecondMove -> true
        else -> false
    }
}
