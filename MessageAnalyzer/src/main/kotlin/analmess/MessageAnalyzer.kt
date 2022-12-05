package analmess

import analmess.Table.Builder.ColumnsAppender
import analmess.Table.Builder.RowsAppender
import analmess.loader.Loader
import analmess.loader.TgParser
import inDictionary
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import partsOfSpeech
import printInfoFromWord
import tokens
import voiceToText
import java.io.File
import kotlin.time.Duration

const val jsonPath = "C:/Users/oQaris/Desktop/Telegram Desktop/Polina/result.json"
const val topCount = 20

val loader: Loader = TgParser(File(jsonPath))
/*VkDownloader(
    254878066,
    "vk1.a.SONOLoNIMEJh5FuwxzIin4IuczBN3PT30EnZMk0qI7d9SOPFOegj7x0tnA_8_cotRH6VRH8jXv-iKzpMqJZauJFOEkqYkXykquAp5F4pAvZjl2jZh9gJEw66QNIyLUfzaU_9W1nG1Er_fKRcUOC_co6rVrlJdrh2yvFQWl8FmaSX3nBJN_G5AWJK5T30cqBG",
    2000000000 + 86
)*/

val chat = //loadChat("analmes/golden")
    loader.loadChat()

val userToMessages = chat.messages.groupBy { it.from }.toSortedMap()

@OptIn(ExperimentalSerializationApi::class)
fun saveChat(fileName: String) {
    val bytes = ProtoBuf.encodeToByteArray(chat)
    File(fileName).writeBytes(bytes)
}

@OptIn(ExperimentalSerializationApi::class)
fun loadChat(fileName: String) =
    ProtoBuf.decodeFromByteArray<Chat>(File(fileName).readBytes())


fun main() {
    voiceToText()
    return

    /*val freqByUser = wordsFrequency(userToMessages["Артём Мухамед-Каримов"]!!)
    freqByUser.filter { it.count > 1 }.map { it.lemma }.toSet()
        .filter { w ->
            w.inDictionary() && WordformMeaning.lookupForMeanings(w).all {
                it.lemma.toString().length == 4 && it.partOfSpeech == PartOfSpeech.Noun
                        && !it.morphology.contains(MorphologyTag.Inanimate)
            }
        }
        .also { println(it.size) }
        .forEach { println(it) }
*/
    //saveChat("analmes/golden")

    generalInfo()
    userSummary()
    tableWords()
    tableReply()
    tableVoiceMessages()
    tableMediaType()
    tablePartsOfSpeech()

    // Отладка

    println("+ Удалённые слова:")
    println(setRemWords.joinToString("\n"))
    println()

    printInfoFromWord("дима")
}

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

    println("- Топ активных дней:")
    val dateFreq = chat.messages.groupingBy { it.date.date }.eachCount()
        .toList().sortedBy { (_, count) -> -count }
        .take(topCount).unzip()
    printTableByColumns {
        add(dateFreq.first)
        add(dateFreq.second)
    }
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

    println("- Популярные слова:")
    printFrequency(chat.messages, topCount)

    println("- Популярные слова по пользователям:")
    printTableByColumns {
        userToMessages.entries.forEach { (user, messages) ->
            val freqByUser = wordsFrequency(messages).take(topCount)
            add(user, buildTableByRows(freqByUser).formattedRows())
        }
    }

    println("- Любимые смайлики:")
    printTableByColumns {
        userToMessages.entries.forEach { (user, messages) ->
            val freqByUser = emojiFrequency(messages).take(topCount)
            add(user, buildTableByRows(freqByUser).formattedRows())
        }
    }
}

fun tableWords() {
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
        val userToLengthsMessage = userToTextMessages
            .mapValues { (_, v) -> v.map { it.length } }
        add(
            "Ср. длина сообщения",
            userToLengthsMessage.map { (_, v) ->
                "%.3f".format(v.mean())
            }
        )
        // Слова
        val userToWordsCount = userToWords
            .mapValues { (_, v) -> v.size }
        add(
            "Количество слов",
            userToWordsCount.values
        )
        add(
            "Слов в сообщении",
            userToWordsCount.map { (k, v) ->
                "%.3f".format(v.toDouble() / userToTextMessages[k]!!.size)
            }
        )
        val vocabulary = userToWords.mapValues { (_, v) ->
            // первые - в словаре, вторые - нет
            v.toSet().partition { it.inDictionary() }
        }
        add(
            "Словарный запас (рус.)",
            vocabulary.values.map { it.first.size }
        )
        //Todo - удалить ссылки и английские слова
        add(
            "Неизвестных слов",
            vocabulary.values.map { it.second.size }
        )
    }
}

fun tableReply() {
    printTableByColumns {
        add(
            "Имя пользователя",
            userToMessages.keys
        )
        add(
            "Пересланных сообщений",
            userToMessages.mapValues { (_, v) -> v.count { it.isForwarded } }.values
        )
        add(
            "Число ответов",
            userToMessages.mapValues { (_, v) -> v.count { it.replyTo != null } }.values
        )
        // Ответы
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
}

fun tableVoiceMessages() {
    val userToDurationVoiceMessages = userToMessages
        .mapValues { entry ->
            entry.value
                .filter { m -> m.attachments.any { it.name == "audio_message" || it.name.startsWith("voice") } }
                .map { it.durationSeconds!! }
        }
        .sortAndCheck(userToMessages.size)

    val emptyToken = "0"
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
                //todo mean
                durs.reduce { acc, dur -> acc + dur } / durs.size
            }
        )
        add(
            "Mdn длина ГС",
            userToDurationVoiceMessages.values.map { durs ->
                if (durs.isEmpty()) return@map emptyToken
                //todo median
                durs.sorted()[durs.size / 2]
            }
        )
    }
}

fun tableMediaType() {
    val userToMediaTypeWithFrequency = userToMessages
        .mapValues { entry ->
            entry.value.flatMap { m -> m.attachments.map { it.name } }.sortedCounter()
        }
        .sortAndCheck(userToMessages.size)

    printTableByColumns {
        userToMediaTypeWithFrequency.forEach { (user, typeToCount) ->
            add(user, buildTableByRows(typeToCount).formattedRows())
        }
    }
}

fun tablePartsOfSpeech() {
    val userToPartsOfSpeechWithFrequency = userToMessages
        .mapValues { entry ->
            entry.value.flatMap { m ->
                m.text.simpleText().tokens().flatMap {
                    it.partsOfSpeech()
                }
            }.sortedCounter()
        }.sortAndCheck(userToMessages.size)

    printTableByColumns {

        val sortedPartsOfSpeech = userToPartsOfSpeechWithFrequency.entries
            .fold(setOf<String>()) { acc, ent -> acc + ent.value.unzip().first }
            .sortedBy { pos ->
                userToPartsOfSpeechWithFrequency.entries.sumOf { posToFreq ->
                    posToFreq.value.firstOrNull { it.first == pos }?.second ?: 0
                }
            }.reversed()

        add("Часть речи", sortedPartsOfSpeech)

        userToPartsOfSpeechWithFrequency.forEach { (user, posToFreq) ->
            val col = buildList {
                sortedPartsOfSpeech.forEach { pos ->
                    val all = posToFreq.sumOf { it.second }.toDouble()
                    val percent = (posToFreq.firstOrNull { it.first == pos }?.second ?: 0) / all
                    add(String.format("%.3f", percent * 100) + " %")
                }
            }
            add(user, col)
        }
    }
}


fun <K : Comparable<K>, V> Map<K, V>.sortAndCheck(reqSize: Int) =
    this.toSortedMap().apply { require(size == reqSize) }

fun printTableByColumns(content: Table.Builder.Appender.() -> Unit) {
    Table.with(padding = 5, appender = ::ColumnsAppender, append = content).print()
    println()
}

fun buildTableByRows(freqByUser: List<WordsFrequency>) =
    Table.with(padding = 1, appender = ::RowsAppender, append = {
        freqByUser.forEach { add(listOf(it.lemma, it.count, it.formatOrig())) }
    })

@JvmName("buildTableByRowsPair")
fun buildTableByRows(freqByUser: List<Pair<Any, Int>>) =
    Table.with(padding = 1, appender = ::RowsAppender, append = {
        freqByUser.forEach { add(listOf(it.first, it.second)) }
    })

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
    frequency: (List<Message>) -> List<WordsFrequency> = ::wordsFrequency
) {
    val freqByUser = frequency(messages)
    val content = freqByUser.take(limit)
    printTableByColumns {
        add(content.map { it.lemma })
        add(content.map { it.count.toString() })
        add(content.map { it.formatOrig() })
    }
    println()
}
