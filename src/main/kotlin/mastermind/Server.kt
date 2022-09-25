package mastermind

private const val len = 4
private const val numAttempts = 7

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
            println("Победа!")
            println("$guess - единственный верный ответ")
            return
        }
        val answer = hardSelector.select(answers, guess)

        val (b, c) = evaluate(guess, answer)
        answers.removeIf { evaluate(it, guess) != b bc c }

        println("Быков: $b, Коров: $c")
    }
    println("Вы не угадали, попытайтесь ещё раз :(")
    println("Загадано было: " + answers.random())
}
