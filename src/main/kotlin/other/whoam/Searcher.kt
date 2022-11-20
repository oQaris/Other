package other.whoam

import org.jsoup.Jsoup

fun main() {
    val secret = "путин"
    val answer = "живая материя"
    val request = ("$secret $answer").replace(" ", "+")
    val strGoogleSearch = "http://www.google.com/search?q="
    Jsoup.connect(strGoogleSearch + request).get().body().html()
        .also { s -> println(s.toCharArray().filter { it in 'А'..'я' || it.isWhitespace() }.joinToString("")) }
}
