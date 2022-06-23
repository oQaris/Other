package seabattle

class Game(
    val firstPlayer: Player,
    val secondPlayer: Player,
) {
    var gameState = GameState.FirstMove

    fun arrange() {
        firstPlayer.arrange()
        secondPlayer.arrange()
    }

    fun run() {
        while (attack() != AnswerType.EndGame) {
        }
    }

    /**
     * Инициирует атаку (ход) текущего игрока на заданную ячейку
     * @return Результат хода (мимо, ранил, убил, конец игры)
     */
    fun attack(): AnswerType {
        if (!gameState.inGame())
            return AnswerType.EndGame

        val (curPlayer, enemyBoard) =
            if (gameState == GameState.FirstMove) firstPlayer to secondPlayer.board
            else secondPlayer to firstPlayer.board

        val target = curPlayer.attackCell(allowedSteps(enemyBoard))
        val answer = enemyBoard.attack(target)

        if (answer == AnswerType.Past)
            gameState = gameState.nextStep()
        else if (enemyBoard.checkLoss()) {
            gameState = gameState.toWin()
            return AnswerType.EndGame
        }
        return answer
    }


    private fun allowedSteps(board: Board): Set<Cell> {
        board.killedShips()
        return emptySet()
    }
}
