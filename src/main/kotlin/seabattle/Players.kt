package seabattle

interface Player {
    val settings: Settings
    val board: Board

    fun arrange()

    fun attackCell(allowedCell: Set<Cell>): Cell
}


class ConsolePlayer(override val settings: Settings) : Player {
    override val board = Board(settings.boardSize)

    override fun arrange() {
        println("Расставьте корабли по шаблону:")
        println("Направление (1-вправо, 2-вниз)")
        println("Направление (1-вправо, 2-вниз)")
    }

    override fun attackCell(allowedCell: Set<Cell>): Cell {
        TODO("Not yet implemented")
    }

}

class Bot(override val settings: Settings) : Player {
    override val board = Board(settings.boardSize)

    override fun arrange() {
        println("Расставьте корабли по шаблону:")
        println("Направление (1-вправо, 2-вниз)")
        println("Направление (1-вправо, 2-вниз)")
    }

    override fun attackCell(allowedCell: Set<Cell>): Cell {
        TODO("Not yet implemented")
    }

}
