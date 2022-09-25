package mastermind

interface Selector {

    /**
     * Выбирает предполагаемый ответ из множества доступных решений
     */
    fun select(answers: MutableList<String>, guess: String): String
}

val hardSelector = object : Selector {
    /**
     * Обратный минимакс - выбирает ответ, который для заданного [guess] даст минимум информации
     * @see mastermind.MinimaxDecoder.nextStep(java.util.List<java.lang.String>, java.util.List<java.lang.String>)
     */
    override fun select(answers: MutableList<String>, guess: String): String {
        return answers.groupBy { evaluate(it, guess) }
            .maxByOrNull { it.value.size }!!.value.random()
    }
}

val randomSelector = object : Selector {
    override fun select(answers: MutableList<String>, guess: String) = answers.random()
}
