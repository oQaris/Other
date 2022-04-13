package mastermind

import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition
import kotlin.math.max

fun main() {
    println("Статистика по играм с размером входа = $len:")
    val answers = answerSequence(len).toList()
    var maxAll = 0
    var avgAll = 0.0

    val src = (0..9).map { it.toString() }
    val brut =
        generatePatterns(src, '0'..'4').keys.plus(
            generatePatterns(src, '1'..'5').keys
        ).plus(
            generatePatterns(src, '2'..'6').keys
        ).plus(
            generatePatterns(src, '3'..'7').keys
        ).plus(
            generatePatterns(src, '4'..'8').keys
        ).plus(
            generatePatterns(src, '5'..'9').keys
        ).permutationsWithRepetition(len).toList()

    brut.forEach { firstGuessList ->
        var maxAttempts = 0
        var sumAttempts = 0
        val firstGuess = firstGuessList.joinToString("")
        println(firstGuess)

        generatePatterns(
            answers, patternsChar = '0'..'9'
        ).forEach { answer ->

            var counter = 0
            //println(answer)
            decoderStarter(answers,
                firstGuess = firstGuess,
                finishAction = {}
            ) { guess ->
                counter++
                evaluate(guess, answer.key)//.also { println("\t$guess  $it") }
            }
            //println("\t" + counter)
            maxAttempts = max(maxAttempts, counter)
            sumAttempts += counter * answer.value
        }
        maxAll = max(maxAll, maxAttempts)
        avgAll += sumAttempts.toDouble() / answers.size
        println("\tМаксимальное число ходов: $maxAttempts")
        println("\tСреднее число ходов: ${sumAttempts.toDouble() / answers.size}")
    }
    println()
    println("Максимальное число ходов по всем ключам: $maxAll")
    println("Среднее число ходов по всем ключам: ${avgAll / brut.size}")
}


fun generatePatterns(
    range: Iterable<String>, patternsChar: CharRange = 'a'..'z'
) = buildList {

    val len = range.maxOf { it.length }
    require(patternsChar.count() >= len)

    range.forEach { num ->
        val str = num.padStart(len)
        val bijection = str.toSet().zip(patternsChar).toMap()

        add(str.map { bijection[it] }.joinToString(""))
    }
}.counting()
