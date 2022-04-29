package analmess

import kotlinx.datetime.*
import java.net.MalformedURLException
import java.net.URL
import kotlin.time.Duration

val setRemWords = mutableSetOf<String>()

fun <T> Iterable<T>.sortedCounter(grouping: (T) -> T = { it }) =
    groupingBy(grouping).eachCount().toList().sortedBy { (_, count) -> -count }

fun <T> Iterable<T>.maxsByCount() =
    sortedCounter().run {
        takeWhile { it.second == first().second }
            .map { it.first }
    }

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
