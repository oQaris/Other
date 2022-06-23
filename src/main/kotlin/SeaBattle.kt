class Board {
    private val size = 10
    private val board = Array(size) { Array(size) { CellType.EmptyHidden } }

    fun addShip(row: Int, col: Int) {
        board[row][col] = CellType.ShipHidden
    }

    fun attack(row: Int, col: Int): AnswerType {
        val curCell =
            if (board[row][col] == CellType.ShipHidden) CellType.Ship
            else CellType.Empty

        board[row][col] = curCell

        return if (curCell == CellType.Empty) AnswerType.Past
        else if (checkKill(row, col)) AnswerType.Killed else AnswerType.Hurt
    }

    //TODO
    private fun checkKill(row: Int, col: Int) = false

    fun checkLoss() = board.all { row -> row.all { it != CellType.ShipHidden } }

    override fun toString() = StringBuilder().apply {
        board.forEach { row ->
            row.forEach {
                append(it.toString())
            }
            append("\n")
        }
    }.toString()
}

enum class CellType {
    EmptyHidden, ShipHidden, Empty, Ship;

    override fun toString() = when (this) {
        EmptyHidden, ShipHidden -> "_"
        Empty -> "*"
        Ship -> "X"
    }
}

data class Cell(val row: Int, val col: Int)

fun parse(str: String): Cell {
    require(str.length == 2)
    val (row, col) = str.uppercase().let { it[0] to it[1] }
    return Cell(row - 'A', col.toString().toInt())
}

enum class AnswerType {
    Past, Hurt, Killed, EndGame
}

class Game {
    val boardFirst = Board()
    val boardSecond = Board()
    var gameState = GameState.FirstMove

    fun place() {
        boardFirst.addShip(0, 0)
        boardFirst.addShip(0, 1)

        boardSecond.addShip(3, 4)
        boardSecond.addShip(4, 4)
        boardSecond.addShip(5, 4)
    }

    fun attack(target: Cell): AnswerType {
        if (!gameState.inGame())
            return AnswerType.EndGame

        val enemyBoard = getEnemyBoard()
        val answer = enemyBoard
            .attack(target.row, target.col)

        if (answer == AnswerType.Past)
            gameState = gameState.nextStep()
        //TODO можно раскомментировать, когда будет реализован checkKill
        else if (/*answer == AnswerType.Killed
            && */enemyBoard.checkLoss()
        ) {
            gameState = gameState.toWin()
            return AnswerType.EndGame
        }
        return answer
    }

    private fun getEnemyBoard() =
        if (gameState == GameState.FirstMove) boardSecond
        else boardFirst
}

enum class GameState {
    FirstMove, SecondMove, FirstWin, SecondWin;

    fun nextStep() = when (this) {
        FirstMove -> SecondMove
        SecondMove -> FirstMove
        FirstWin, SecondWin -> throw UnsupportedOperationException()
    }

    fun toWin() = when (this) {
        FirstMove -> FirstWin
        SecondMove -> SecondWin
        FirstWin, SecondWin -> throw UnsupportedOperationException()
    }

    fun inGame() = when (this) {
        FirstMove, SecondMove -> true
        FirstWin, SecondWin -> false
    }
}

fun main() {
    val game = Game()
    game.place()

    while (true) {
        val answer = game.attack(parse(readln()))
        when (answer) {
            AnswerType.Past -> println("Мимо")
            AnswerType.Hurt -> println("Ранил")
            AnswerType.Killed -> println("Убил")
            AnswerType.EndGame -> {
                println("Игра закончена - ${game.gameState}")
                return
            }
        }
        println("Доска первого игрока:")
        println(game.boardFirst)
        println("Доска второго игрока:")
        println(game.boardSecond)
    }
}
