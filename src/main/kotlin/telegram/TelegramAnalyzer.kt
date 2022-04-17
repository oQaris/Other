package telegram

import com.github.demidko.aot.WordformMeaning
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlin.time.Duration

const val jsonPath = "C:/Users/oQaris/Downloads/Telegram Desktop/Dima/result.json"
const val topCount = 20

val chat = Parser(File(jsonPath)).parseChat()
val userToMessages = chat.messages.groupBy { it.from }.toSortedMap()

fun generalInfo() {
    println("${chat.name} - ${chat.type}\n")

    print("Всего сообщений:  ")
    println(chat.messages.size)

    print("Первое сообщение:  ")
    val firstDate = chat.messages.first().date
    println(firstDate)

    print("Длительность общения:  ")
    val lastDate = chat.messages.last().date
    println(duration(firstDate, lastDate))

    print("Дней общения:  ")
    val dayWithMess = chat.messages.map { it.date.date }.toSet().size
    println(dayWithMess)

    print("Сообщений в день:  ")
    println(chat.messages.size / dayWithMess)
    println()
}

fun userSummary() {
    println("- Популярные сообщения:")
    val content = chat.messages.map { it.text.simpleText() }
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
    printFrequency(chat.messages, topCount)
    println()

    println("- Популярные слова по пользователям:")
    printTableByColumns {
        userToMessages.entries.forEach { (user, messages) ->
            val freqByUser = wordsFrequency(messages).take(topCount)
            add(user, freqByUser.map { (w, c) -> "$w  ($c)" })
        }
    }
    println()

    println("- Любимые смайлики:")
    printTableByColumns {
        userToMessages.entries.forEach { (user, messages) ->
            val freqByUser = emojiFrequency(messages).take(topCount)
            add(user, freqByUser.map { (w, c) -> "$w  ($c)" })
        }
    }
    println()
}

fun tableWordsAndReply() {
    val userToTextMessages = userToMessages.mapValues { (_, v) ->
        v.map { it.text.simpleText() }
    }.sortAndCheck(userToMessages.size)

    val userToWords = userToMessages.mapValues { (_, v) ->
        v.flatMap { it.text.simpleText().tokens() }
    }.sortAndCheck(userToMessages.size)

    printTableByColumns {
        add(
            "Имя пользователя",
            userToMessages.keys
        )
        add(
            "Кол-во сообщений",
            userToMessages.map { (_, v) -> v.size }
        )
        // Слова
        val userToWordsCount = userToWords
            .sortAndCheck(userToMessages.size)
            .mapValues { (_, v) -> v.size }
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
        // Ответы
        add(
            "Число ответов",
            userToMessages.mapValues { (_, v) -> v.count { it.reply_to_message_id != null } }.values
        )
        val userToAnswerTimes =
            userToDurationAnswer(chat.messages)
                .groupBy { it.first }
                .sortAndCheck(userToMessages.size)
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
}

fun tableVoiceMessages() {
    val userToDurationVoiceMessages = userToMessages
        .mapValues { m ->
            m.value
                .filter { it.media_type == "voice_message" }
                .map { it.duration_seconds!! }
        }
        .sortAndCheck(userToMessages.size)

    val emptyToken = "-"
    printTableByColumns {
        add(
            "Имя пользователя",
            userToMessages.keys
        )
        add(
            "Кол-во голосовых",
            userToDurationVoiceMessages.values.map { it.size }
        )
        add(
            "Общая длина ГС",
            userToDurationVoiceMessages.values.map { durs -> durs.sumOf { it } }
        )
        add(
            "Max длина ГС",
            userToDurationVoiceMessages.values.map { durs -> durs.maxOfOrNull { it } ?: emptyToken }
        )
        add(
            "Min длина ГС",
            userToDurationVoiceMessages.values.map { durs -> durs.minOfOrNull { it } ?: emptyToken }
        )
        add(
            "Avg длина ГС",
            userToDurationVoiceMessages.values.map { durs ->
                if (durs.isEmpty()) return@map emptyToken
                durs.reduce { acc, dur -> acc + dur } / durs.size
            }
        )
        add(
            "Mdn длина ГС",
            userToDurationVoiceMessages.values.map { durs ->
                if (durs.isEmpty()) return@map emptyToken
                durs.sorted()[durs.size / 2]
            }
        )
    }
    println()
}

fun tableMediaType() {
    val userToMediaTypeWithFrequency = userToMessages
        .mapValues { m ->
            m.value.map { it.media_type ?: "none" }.sortedCounter()
        }
        .sortAndCheck(userToMessages.size)

    printTableByColumns {
        userToMediaTypeWithFrequency.forEach { (user, typeToCount) ->
            add(user, typeToCount.map { (t, c) -> "$t  ($c)" })
        }
    }
    println()
}

@OptIn(ExperimentalSerializationApi::class)
fun main() {

    generalInfo()
    userSummary()
    tableWordsAndReply()
    tableVoiceMessages()
    tableMediaType()

// Отладка

    /*println("+ Удалённые слова:")
    println(setRemWords.joinToString("\n"))
    println()

    println("+ Ссылки:")
    println(setUrl.joinToString("\n"))
    println()

    printInfoFromWord("я")
    printInfoFromWord("меня")*/
}

fun <K : Comparable<K>, V> Map<K, V>.sortAndCheck(reqSize: Int) =
    this.toSortedMap().apply { require(size == reqSize) }

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
