package telegram

import com.github.demidko.aot.WordformMeaning
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlin.time.Duration

const val jsonPath = "C:/Users/oQaris/Downloads/Telegram Desktop/Dima/result.json"
const val topCount = 20

@OptIn(ExperimentalSerializationApi::class)
fun main() {

    val parser = Parser(File(jsonPath))
    val allMessages = parser.parseChat().messages

    print("Уже общаются:  ")
    val dist = distant(allMessages.last().date, allMessages.first().date)
    println(dist)
    print("Сообщений в день:  ")
    println(allMessages.size / dist.inWholeDays)
    println()

    println("- Популярные сообщения:")
    val content = allMessages.map { it.text.simpleText() }
        .filter { it.isNotBlank() }
        .sortedCounter()
        .take(topCount)
        .filter { it.second > 1 }
    printTable {
        add(content.map { it.first })
        add(content.map { it.second.toString() })
    }
    println()

    println("- Популярные слова:")
    printFrequency(allMessages, topCount)
    println()

    println("- Популярные слова по пользователям:")
    printTable {
        allMessages.groupBy { it.from }
            .entries.forEach { (user, messages) ->
                val freqByUser = wordsFrequency(messages).take(topCount)
                addColumn(user, freqByUser.map { (w, c) -> "$w  ($c)" })
            }
    }
    println()

    println("- Любимые смайлики:")
    printTable {
        allMessages.groupBy { it.from }
            .entries.forEach { (user, messages) ->
                val freqByUser = emojiFrequency(messages).take(topCount)
                addColumn(user, freqByUser.map { (w, c) -> "$w  ($c)" })
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
    printTable {
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
        addColumn(
            "Число ответов",
            userToMessages.mapValues { (_, v) -> v.count { it.reply_to_message_id != null } }.values
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
            userToAnswerTimes.values.map { times -> times.reduce { acc, dur -> acc + dur } / times.size }
        )
        addColumn(
            "Mdn время ответа",
            userToAnswerTimes.values.map { times -> times.sorted()[times.size / 2] }
        )
    }
    println()

    println("+ Удалённые слова:")
    println(setRemWords.joinToString("\n"))
    println()

    println("+ Ссылки:")
    println(setUrl.joinToString("\n"))
    println()
}

fun printTable(content: Table.() -> Unit) {
    Table(padding = 5).apply {
        this.content()
    }.print()
}

fun printInfoFromWord(word: String) {
    val means = WordformMeaning.lookupForMeanings(word)
    print("$word ")
    if (means.isNotEmpty()) {
        val mean = means[0]
        print("${mean.lemma} ${mean.morphology} ${mean.partOfSpeech} ${mean.transformations}")
    } else print("None")
    println()
}

fun userToDurationAnswer(messages: List<Message>): List<Pair<String, Duration>> = buildList {
    var prevUser = messages.first().from
    var prevTime = messages.first().date
    messages.forEach { message ->
        val user = message.from
        if (prevUser != user) {
            val period = distant(message.date, prevTime)
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
    val content3 = freqByUser.take(limit)
    Table(padding = 5).apply {
        add(content3.map { it.first })
        add(content3.map { it.second.toString() })
    }.print()
}
