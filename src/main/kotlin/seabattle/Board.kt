package seabattle

class Board(val size: Int = 10) {
    private val board = Array(size) { Array(size) { CellType.EmptyHidden } }
    private val ships = mutableSetOf<ShipExtended>()

    /**
     * Добавление нового корабля на доску
     */
    fun addShip(ship: Ship) {
        val innerShip = ship.toExtended()
        require(isCorrect(innerShip)) { "Пересечение кораблей!" }
        ships.add(innerShip)

        innerShip.cells.forEach { (row, col) ->
            board[row][col] = CellType.ShipHidden
        }
    }

    /**
     * Сделать ход и получить его результат
     */
    fun attack(target: Cell): AnswerType {
        requireCell(target)

        val (row, col) = target
        val curType = board[row][col].unhidden()
        board[row][col] = curType

        return if (curType == CellType.Empty) AnswerType.Past
        else
            if (isKill(target)) AnswerType.Killed
            else AnswerType.Hurt
    }

    /**
     * Проверка того, что новый корабль не граничит не с одним из существующих
     */
    private fun isCorrect(newShip: ShipExtended): Boolean {
        val fatShip = buildSet {
            newShip.cells.forEach {
                for (i in -1..1)
                    for (j in -1..1)
                        add(Cell(it.row + i, it.col + j))
            }
        }
        return ships.all { curShip ->
            !curShip.cells.any { it in fatShip }
        }
    }

    /**
     * Проверят, что не осталось скрытых кораблей
     */
    fun checkLoss() = ships.isNotEmpty()
            && board.all { row -> row.all { it != CellType.ShipHidden } }

    fun killedShips() = ships.filter { it.isKill }

    /**
     * Определяет, что корабль на заданной ячейке полностью состоит из открытых.
     * Если так, то устанавливает флаг убитости на корабль
     */
    private fun isKill(target: Cell): Boolean {
        val ship = ships.first { target in it.cells }
        return ship.cells.all { !board[it.row][it.col].isHidden() }
            .also { if (it) ship.isKill = true }
    }

    data class ShipExtended(val cells: Set<Cell>, var isKill: Boolean = false)

    private fun Ship.toExtended(): ShipExtended {
        var (row, col) = startCell.row to startCell.col

        return ShipExtended(buildSet {
            repeat(len) {
                val newCell = Cell(row, col)
                requireCell(newCell)
                add(newCell)

                if (isHorizontal) col++
                else row++
            }
        })
    }

    private fun requireCell(cell: Cell) {
        val range = 0 until size
        require(cell.row in range && cell.col in range) { "Выход за границы доски!" }
    }

    override fun toString() = StringBuilder().apply {
        board.forEach { row ->
            row.forEach { append(it.toString()) }
            append("\n")
        }
    }.toString()
}
