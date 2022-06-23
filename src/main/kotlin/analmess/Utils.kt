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

fun <T : Number> Collection<T>.mean() = this.sumOf { it.toDouble() } / this.size

/*
* @return Среднее арифметическое временных меток
fun getMean(): Long {
    if (times.isEmpty()) return 0L
    return times.sum() / times.size
}

*/
/** @return Медиану временных меток *//*

fun getMedian(): Long {
    if (times.isEmpty()) return 0
    val sortTimes = times.sorted()
    val n = times.size
    return if (n % 2 == 1) sortTimes[(n - 1) / 2]
    else (sortTimes[n / 2 - 1] + sortTimes[n / 2]) / 2
}

*/
/** @return Моду временных меток *//*

fun getMode(): Long {
    return times.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: 0L
}*/
