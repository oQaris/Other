package other.utils

import com.github.shiguruikai.combinatoricskt.permutationsWithRepetition
import org.jsoup.Jsoup

fun main() {
    repeat(4) { n_ ->
        val n = n_ + 1
        println("Длина префикса - $n")
        ('a'..'z').toList()
            .permutationsWithRepetition(n)
            .filter { prefixList ->
                val prefix = prefixList.joinToString("")
                val response = Jsoup.connect("https://t.me/${prefix}lollll").get()
                !response.head().toString()
                    .contains("<meta property=\"og:description\" content=\"\">")
                        && !response.body().toString()
                    .contains("subscriber")
                        && !response.body().toString()
                    .contains("member")
            }.onEach { println(it) }
            .count().let { println("Всего:$it") }
    }
}
