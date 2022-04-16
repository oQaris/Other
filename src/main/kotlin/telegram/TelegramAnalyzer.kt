package telegram

import com.github.demidko.aot.WordformMeaning
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlin.time.Duration

const val jsonPath = /*"data/chat.json"*/"C:/Users/oQaris/Downloads/Telegram Desktop/Dima/result.json"
const val topCount = 20

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val parser = Parser(File(jsonPath))
    val chat = parser.parseChat()

    println("${chat.name} - ${chat.type}\n")
    val allMessages = chat.messages

    print("Всего сообщений:  ")
    println(allMessages.size)

    print("Первое сообщение:  ")
    val firstDate = allMessages.first().date
    println(firstDate)

    print("Длительность общения:  ")
    val lastDate = allMessages.last().date
    println(duration(firstDate, lastDate))

    print("Дней общения:  ")
    val dayWithMess = allMessages.map { it.date.date }.toSet().size
    println(dayWithMess)

    print("Сообщений в день:  ")
    println(allMessages.size / dayWithMess)

    println()

    println("- Популярные сообщения:")
    val content = allMessages.map { it.text.simpleText() }
        .filter { it.isNotBlank() }
        .sortedCounter()
        .take(topCount)
        .filter { it.second > 1 }
    printTableByColumns {
        add(content.map { it.first })
        add(content.map { it.second.toString() })
    }
    println()

    println("- Популярные слова:")
    printFrequency(allMessages, topCount)
    println()

    println("- Популярные слова по пользователям:")
    printTableByColumns {
        allMessages.groupBy { it.from }
            .entries.forEach { (user, messages) ->
                val freqByUser = wordsFrequency(messages).take(topCount)
                add(user, freqByUser.map { (w, c) -> "$w  ($c)" })
            }
    }
    println()

    println("- Любимые смайлики:")
    printTableByColumns {
        allMessages.groupBy { it.from }
            .entries.forEach { (user, messages) ->
                val freqByUser = emojiFrequency(messages).take(topCount)
                add(user, freqByUser.map { (w, c) -> "$w  ($c)" })
            }
    }
    println()

    val userToMessages = allMessages.groupBy { it.from }
    val userToTextMessages = userToMessages.mapValues { (_, v) ->
        v.map { it.text.simpleText() }
    }
    val userToWords = userToMessages.mapValues { (_, v) ->
        v.flatMap { it.text.simpleText().tokens() }
    }
    printTableByColumns {
        add(
            "Имя пользователя",
            userToTextMessages.keys
        )
        add(
            "Кол-во сообщений",
            userToTextMessages.map { (_, v) -> v.size }
        )
        val userToWordsCount = userToWords.mapValues { (_, v) -> v.size }
        add(
            "Количество слов",
            userToWordsCount.values
        )
        add(
            "Слов в сообщении".replaceFirstChar { it.uppercase() },
            userToWordsCount.entries.map { (k, v) ->
                "%.3f".format(v.toDouble() / userToTextMessages[k]!!.size)
            }
        )
        add(
            "Словарный запас",
            userToWords.mapValues { (_, v) -> v.toSet().size }.values
        )
        add(
            "Число ответов",
            userToMessages.mapValues { (_, v) -> v.count { it.reply_to_message_id != null } }.values
        )
        val userToAnswerTimes =
            userToDurationAnswer(allMessages).groupBy { it.first }
                .mapValues { times -> times.value.map { it.second } }
        add(
            "Max время ответа",
            userToAnswerTimes.values.map { times -> times.maxOf { it } }
        )
        add(
            "Min время ответа",
            userToAnswerTimes.values.map { times -> times.minOf { it } }
        )
        add(
            "Avg время ответа",
            userToAnswerTimes.values.map { times -> times.reduce { acc, dur -> acc + dur } / times.size }
        )
        add(
            "Mdn время ответа",
            userToAnswerTimes.values.map { times -> times.sorted()[times.size / 2] }
        )
    }
    println()

    // Отладка

    println("+ Удалённые слова:")
    println(setRemWords.joinToString("\n"))
    println()

    println("+ Ссылки:")
    println(setUrl.joinToString("\n"))
    println()

    printInfoFromWord("я")
    printInfoFromWord("меня")
}

fun printTableByRows(content: Table.Builder.Appender.() -> Unit) {
    Table.withRows(padding = 5, append = content).print()
}

fun printTableByColumns(content: Table.Builder.Appender.() -> Unit) {
    Table.withColumns(padding = 5, append = content).print()
}

fun printInfoFromWord(word: String) {
    println(word)
    var flag = false
    WordformMeaning.lookupForMeanings(word).forEach { mean ->
        println("\t${mean.lemma} ${mean.morphology} ${mean.partOfSpeech} ${mean.transformations}")
        flag = true
    }
    if (!flag) println("None")
}

fun userToDurationAnswer(messages: List<Message>): List<Pair<String, Duration>> = buildList {
    var prevUser = messages.first().from
    var prevTime = messages.first().date
    messages.forEach { message ->
        val user = message.from
        if (prevUser != user) {
            val period = duration(message.date, prevTime)
            add(user to period)
        }
        prevUser = user
        prevTime = message.date
    }
}

fun printFrequency(
    messages: List<Message>,
    limit: Int,
    frequency: (List<Message>) -> List<Pair<String, Int>> = ::wordsFrequency
) {
    val freqByUser = frequency(messages)
    val content = freqByUser.take(limit)
    printTableByColumns {
        add(content.map { it.first })
        add(content.map { it.second.toString() })
    }
}
