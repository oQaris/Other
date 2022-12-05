import java.net.MalformedURLException
import java.net.URL

fun <T> Iterable<T>.sortedCounter(grouping: (T) -> T = { it }) =
    groupingBy(grouping).eachCount().toList().sortedBy { (_, count) -> -count }

fun <T> Iterable<T>.maxsByCount() =
    sortedCounter().run {
        takeWhile { it.second == first().second }
            .map { it.first }
    }

fun String.isURL() = try {
    URL(this); true
} catch (e: MalformedURLException) {
    false
}
