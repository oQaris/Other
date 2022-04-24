package telegram

import com.github.demidko.aot.WordformMeaning
import com.vdurmont.emoji.EmojiParser
import kotlinx.datetime.*
import java.net.MalformedURLException
import java.net.URL
import kotlin.time.Duration

val setRemWords = mutableSetOf<String>()

fun <T> Iterable<T>.sortedCounter() = groupingBy { it }.eachCount().toList().sortedBy { (_, count) -> -count }

fun <K, V> Iterable<Pair<K, V>>.tableStr(n: Int = 20) = take(n).joinToString("\n") { (k, v) -> "$k\t\t$v" }

fun toLemma(str: String): String {
    return try {
        WordformMeaning.lookupForMeanings(str)[0].lemma.toString()
    } catch (e: Exception) {
        str
    }
}

fun wordsFrequency(mess: List<Message>): List<WordsFrequency> {
    val wordToLemma = mess.flatMap {
        val start = it.text.simpleText().tokens()
        val end = start.removeSparePartsOfSpeech()
        //log
        setRemWords.addAll(start.minus(end.toSet()).map { m -> toLemma(m) })
        end.zip(end.lemmas())
    }
    return wordToLemma.map { it.second }.sortedCounter().map { (lem, cnt) ->
        val src = wordToLemma.filter { it.second == lem }
            .map { it.first }.sortedCounter().first().first
        WordsFrequency(lem, cnt, if (src != lem) "~$src" else "")
    }
}

data class WordsFrequency(val lemma: String, val count: Int, val src: String)

fun emojiFrequency(mess: List<Message>) = mess.flatMap {
    EmojiParser.extractEmojis(it.text.simpleText())
}.sortedCounter()

fun duration(date1: LocalDateTime, date2: LocalDateTime): Duration {
    val maxToMin = if (date1 > date2) date1 to date2 else date2 to date1
    return maxToMin.first.toInstant(TimeZone.UTC) - maxToMin.second.toInstant(TimeZone.UTC)
}

fun nowDate() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

fun String.isURL() = try {
    URL(this); true
} catch (e: MalformedURLException) {
    false
}
