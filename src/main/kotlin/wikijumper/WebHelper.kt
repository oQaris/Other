package wikijumper

import mu.KotlinLogging
import org.jsoup.Jsoup
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder

private val logger = KotlinLogging.logger {}

fun getChild(locale: String, shortLink: String): List<String> = try {
    logger.debug { "[visit] $shortLink" }
    val mainPage = Jsoup.connect("https://$locale.wikipedia.org/wiki/").get().location().drop(24)
    val escapedLink = // Экранируем специальные символы в ссылке
        URI.create("https://$locale.wikipedia.org/wiki/${encode(shortLink)}").toString()

    val doc = Jsoup.connect(escapedLink)
        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
        .referrer("http://www.google.com")
        .ignoreHttpErrors(true)
        .timeout(50 * 1000) // Увеличить значение при плохом интернете
        .get()

    doc.select("a[href^=/wiki/]").eachAttr("href")
        .asSequence().distinct()
        .minusElement(mainPage) // Удаляем главную страницу, чтоб не было читерно
        .filterNot { it.contains(':') } // Исключаем файлы и специальные страницы
        .map { it.substring(6) } // убираем /wiki/, чтоб не занимало память
        .map { link -> link.takeWhile { it != '#' } } // Удаляем якоря
        .map { decode(it) }.toList()
} catch (e: Exception) {
    logger.warn { "[skipped] $shortLink" }
    emptyList()
}

fun buildPath(endPage: WebPage) = buildList {
    var page: WebPage? = endPage
    while (page != null) {
        add(page.url)
        page = page.parent
    }
}.asReversed()

fun decode(link: String): String {
    return URLDecoder.decode(link, "UTF-8")
}

fun encode(link: String): String {
    return URLEncoder.encode(link, "UTF-8")
}
