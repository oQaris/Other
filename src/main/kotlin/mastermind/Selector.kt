package mastermind

interface Selector {

    /**
     * Выбирает предполагаемый ответ из множества доступных решений
     */
    fun select(answers: MutableList<String>, guess: String): String
}
