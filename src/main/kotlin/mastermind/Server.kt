package mastermind

// implementation("com.github.shiguruikai:combinatoricskt:1.6.0")
import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition
import kotlin.math.min

const val len = 4
const val numAttempts = 7

fun main() {
    println(
        "Добро пожаловать в игру Mastermind (или по-русски \"Быки и Коровы\")!\n" +
                "Вам предстоит угадать строку длины $len, состоящую из цифр,\n" +
                "основываясь лишь на числе точных совпадений (быков)\n" +
                "и количестве цифр не на своём месте (коров).\n" +
                "Число попыток ограничено $numAttempts. Желаю удачи!\n"
    )
    val answers = answerSequence(len).toMutableList()

    repeat(numAttempts) { att ->

        println("${att + 1}) Введите свою догадку:")
        val guess = readln().trim()
        if (guess.length != len || !guess.all { it.isDigit() }) {
            println("Некорректный ввод!")
            return
        }

        answers.remove(guess)
        if (answers.isEmpty()) {
            println("flag{MasterM1nd_VeryNice777}")
            return
        }
        val answer = answers.random()

        val (b, c) = evaluate(guess, answer)
        answers.removeIf { evaluate(it, guess) != b bc c }

        println("Быков: $b, Коров: $c")
    }
    println("Вы не угадали, попытайтесь ещё раз :(")
}

/**
 * Последовательность всевозможных строк длины [len], содержащих числа из [range]
 */
fun answerSequence(len: Int, range: IntRange = 0..9): Sequence<String> {
    return range.permutationsWithRepetition(len).map { it.joinToString("") }
}

/**
 * Число быков и коров для текущей догадки и
 * evaluate(a, b) == evaluate(b, a)
 */
fun evaluate(guess: String, answer: String): BullsAndCows {
    val counterAnswer = answer.toList().counting()

    val matches = guess.toList().counting().entries
        .sumOf { (t, u) -> min(u, counterAnswer[t] ?: 0) }

    val bulls = guess.zip(answer).count { it.first == it.second }
    return bulls bc matches - bulls
}
