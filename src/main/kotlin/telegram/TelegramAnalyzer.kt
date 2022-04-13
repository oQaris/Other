package telegram

import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlin.time.Duration

const val jsonPath = "C:/Users/oQaris/Downloads/Telegram Desktop/Sneg/result.json"
const val topCount = 5
const val minLenWord = 3

@OptIn(ExperimentalSerializationApi::class)
fun main() {

    val parser = Parser(File(jsonPath))
    val allMessages = parser.parseMessages()

    println("Популярные сообщения:")
    val messageFrequency = allMessages.map { it.text.simpleText() }.sortedCounter()
    val content = messageFrequency.take(topCount)
    Table(padding = 5).apply {
        add(content.map { it.first })
        add(content.map { it.second.toString() })
    }.print()
    println()

    println("Популярные слова:")
    printWordsFrequency(allMessages, topCount)
    println()

    println("Популярные слова по пользователям:")
    allMessages.groupBy { it.from }.entries.forEach { (user, messages) ->
        println(user)
        printWordsFrequency(messages, topCount)
        println()
    }

    val userToMessages = allMessages.groupBy { it.from }
    val userToTextMessages = userToMessages.mapValues { (_, v) ->
        v.map { it.text.simpleText() }
    }
    val userToWords = userToMessages.mapValues { (_, v) ->
        v.flatMap { toWords(it.text.simpleText()) }
    }
    Table(padding = 5).apply {
        addColumn(
            "Имя пользователя",
            userToTextMessages.keys
        )
        addColumn(
            "Кол-во сообщений",
            userToTextMessages.map { (_, v) -> v.size }
        )
        val userToWordsCount = userToWords.mapValues { (_, v) -> v.size }
        addColumn(
            "Количество слов",
            userToWordsCount.values
        )
        addColumn(
            "Слов в сообщении".replaceFirstChar { it.uppercase() },
            userToWordsCount.entries.map { (k, v) ->
                "%.3f".format(v.toDouble() / userToTextMessages[k]!!.size)
            }
        )
        addColumn(
            "Словарный запас",
            userToWords.mapValues { (_, v) -> v.toSet().size }.values
        )
        val userToAnswerTimes =
            userToDurationAnswer(allMessages).groupBy { it.first }
                .mapValues { times -> times.value.map { it.second } }
        addColumn(
            "Max время ответа",
            userToAnswerTimes.values.map { times -> times.maxOf { it } }
        )
        addColumn(
            "Min время ответа",
            userToAnswerTimes.values.map { times -> times.minOf { it } }
        )
        addColumn(
            "Avg время ответа",
            userToAnswerTimes.values.map { times -> times.reduce { acc, dur -> acc + dur }.div(times.size) }
        )
        addColumn(
            "Mdn время ответа",
            userToAnswerTimes.values.map { times -> times.sorted()[times.size / 2] }
        )
    }.print()
    println()


    val meanings = lookupForMeanings("стали")
    println(meanings.toString())
    println(meanings.joinToString("\n") {
        "${it.lemma} ${it.morphology} ${it.partOfSpeech} ${it.transformations}"
    })
}

fun userToDurationAnswer(messages: List<Message>): List<Pair<String, Duration>> = buildList {
    var prevUser = messages.first().from
    var prevTime = messages.first().date
    messages.forEach { message ->
        val user = message.from
        if (prevUser != user) {
            val period = message.date.toInstant(TimeZone.UTC) - prevTime.toInstant(TimeZone.UTC)
            add(user to period)
        }
        prevUser = user
        prevTime = message.date
    }
}

fun printWordsFrequency(messages: List<Message>, limit: Int) {
    val wordFreqByUser = wordsFrequency(messages)
    val content3 = wordFreqByUser.take(limit)
    Table(padding = 5).apply {
        add(content3.map { it.first })
        add(content3.map { it.second.toString() })
    }.print()
}
