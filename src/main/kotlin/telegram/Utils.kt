package telegram

import com.github.demidko.aot.WordformMeaning
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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

fun wordsFrequency(mess: List<Message>) = mess.flatMap {
    val start = it.text.simpleText().tokens()
    val end = start.removeAuxiliaryPartsOfSpeech()
    setRemWords.addAll(start.minus(end.toSet()).map { m -> toLemma(m) })
    end.lemmas()
}.sortedCounter()

fun emojiFrequency(mess: List<Message>) = mess.flatMap {
    it.text.simpleText().tokens().emoji()
}.sortedCounter()


fun distant(date1: LocalDateTime, date2: LocalDateTime): Duration {
    val maxToMin = if (date1 > date2) date1 to date2 else date2 to date1
    return maxToMin.first.toInstant(TimeZone.UTC) - maxToMin.second.toInstant(TimeZone.UTC)
}

fun String.isURL() = try {
    URL(this); true
} catch (e: MalformedURLException) {
    false
}
