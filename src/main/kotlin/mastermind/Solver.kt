package mastermind

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main() {

    println(generatePatterns((0 until 1_000).toList()
        .map { it.toString() })
        .entries.sortedBy { (_, v) -> v }
        .joinToString("\n") { (k, v) -> "$k $v" })
}

fun decoderStarter(
    answers: List<String>,
    firstGuess: String? = null,
    finishAction: (String) -> Unit = {},
    encoderAction: (String) -> BullsAndCows
) {
    val decoder = MinimaxDecoder(answers, firstGuess)

    decoder.firstGuess()
    while (!decoder.isWin) {

        val answer = encoderAction(decoder.curGuess)
        decoder.nextGuess(answer)
    }
    finishAction(decoder.curGuess)
}

private class MinimaxDecoder(
    private val allAnswers: List<String>,
    private val firstGuess: String? = null
) {
    private lateinit var possibleAnswers: MutableList<String>
    private lateinit var state: ResultGuess

    val curGuess: String
        get() = state.guess

    val isWin: Boolean
        get() = state.isWin

    fun firstGuess() {
        possibleAnswers = allAnswers.toMutableList()
        state = if (firstGuess == null)
            loadOrSaveFirstGuess()
        else ResultGuess(firstGuess, false)
    }

    fun nextGuess(answer: BullsAndCows) {
        possibleAnswers.removeAll {
            evaluate(it, curGuess) != answer
        }
        require(possibleAnswers.isNotEmpty()) { "Противоречивые данные" }
        state = nextStep()
    }

    private fun nextStep() = nextStep(possibleAnswers, allAnswers)

    /**
     * Вычисляет следующую версию ответа, при котором число удаляемых вариантов будет
     * максимальным в худшем случае (По правилу Минимакса)
     */
    private fun nextStep(
        available: List<String>,
        all: List<String>
    ): ResultGuess {
        require(available.isNotEmpty() && all.isNotEmpty())

        if (available.size == 1)
            return ResultGuess(available.single(), true)

        // Чтобы уменьшить максимальное число шагов, ищем ответы в all
        val guess = all.minByOrNull { g ->
            available.map { s ->
                evaluate(g, s)
            }.counting()
                .values.maxOrNull()!!
        }!!
        return ResultGuess(guess, false)
    }

    private fun loadOrSaveFirstGuess(): ResultGuess {
        val file = File("mastermind.json")
        val key = allAnswers.hashCode()
        var mapFirstSteps: MutableMap<Int, ResultGuess>? = null
        if (file.exists()) {
            val jsonTxt = file.readText()
            mapFirstSteps = Json.decodeFromString(jsonTxt)
        }
        mapFirstSteps = mapFirstSteps ?: mutableMapOf()
        return mapFirstSteps[key] ?: nextStep().also {
            mapFirstSteps[key] = it
            val jsonTxt = Json.encodeToString(mapFirstSteps)
            file.writeText(jsonTxt)
        }
    }
}
