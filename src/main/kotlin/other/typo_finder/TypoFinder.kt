package other.typo_finder

import analmess.*
import ru.amayakasa.linguistic.YandexSpeller
import ru.amayakasa.linguistic.parameters.Language
import ru.amayakasa.linguistic.parameters.ResponseInterface
import ru.amayakasa.linguistic.parameters.Version
import java.io.File
import java.util.concurrent.TimeUnit

fun main() {
    // Какую директорию перебирать
    val root = File("C:\\Users\\oQaris\\Desktop\\Git")

    //allExts(root).sortedCounter()
    //    .forEach { println(it.first + '\t' + it.second) }

    var timeMark = System.currentTimeMillis()
    fun getDurationSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeMark)
        .also { timeMark = System.currentTimeMillis() }

    val allTypos = mutableListOf<String>()
    println("Запущен поиск комментариев...")

    File("list_file_typos.txt").printWriter().apply {
        println("Search for typos in $root\nin files with extensions:\n" + exts.joinToString())

        var counter = 0
        fun chatProgress() {
            if (counter != 0 && counter % 100 == 0)
                println("Обработано $counter файлов. Найдено ${allTypos.size} потенциальных опечаток.")
            counter++
        }
        sequenceFiles(root, extsFilter).forEach { file ->
            /*val typoWords = file.bufferedReader()
                .lineSequence().flatMap { line ->
                    line.rusWords().filterNot {
                        it.inDictionary() //|| inExtraDict(it)
                    }
                }.toList()*/

            val comments = file.readText()
                .comments(CommentRegex.JAVA)
                .map { it.trim() }
                .map { it.replace("\\s+".toRegex(), " ") }

            val rusTypoComments = comments.filter { com ->
                com.rusWords().any { !it.inDictionary() }
            }

            allTypos += rusTypoComments

            if (rusTypoComments.isNotEmpty()) {
                println(file.absolutePath)
                rusTypoComments.toSet().forEach { println(it) }
                println()
            }
            chatProgress()
        }
        chatProgress()
    }.flush()

    println("Все файлы обработаны за ${getDurationSec()} секунд.\nЗапущена доп.проверка опечаток с помощью Yandex.Speller...")

    var counter = 0
    File("report_typos.csv").printWriter().apply {
        println("Total typos:;" + allTypos.size + ';')
        println("Unique typos:;" + allTypos.toSet().size + ';')
        println("Sorted list of typos:;;")
        allTypos.sortedCounter().forEach { (word, count) ->
            //todo добавить исправление всего предложения, а не первого слова
            val correct = YandexSpeller(Version.SPELLER_LATEST, ResponseInterface.SPELLER_JSON)
                .getSpelledPhrase(word, Language.RUSSIAN).misspelledWords.firstOrNull()?.variants?.first() ?: ""
            if (correct.isNotEmpty()) {
                println("$word;$correct;$count")
                counter++
            }
        }
        println()
    }.flush()
    println("Все найденные опечатки обработаны за ${getDurationSec()} секунд.\nПодтверждённых - $counter")
}

val exts = setOf(
    /*"txt",
    "MF",
    "md",
    "toml",
    "properties",
    "config",
    "yml",
    "iml",
    "gitignore",
    "csv",
    "xml",
    "json",*/
    "kt",
    "kts",
    "java",
    //"py"
)

// Какие файлы просматривать
val extsFilter = { f: File ->
    // с такими расширениями
    exts.any { f.name.endsWith(".$it") }
    // без такого в названии
    //&& setOf("values")
    //.any { f.name.contains(it) }
}

val extraDictionary = setOf(
    "рефакторинг",
    "токен",
    "перевзвешива",
    "весовки",
    "префлоп",
    "флоп",
    "постфлоп",
    "фолд",
    "блайнд",
    "рейз",
    "нейросет",
    "ривер",
    "селфпле",
    "синглтон",
    "лог",
    "сериализац",
    "десериализац",
    "ситаут",
    "коллюдер",
    "шоудаун",
    "оффлайн",
    "лимпер",
    "техчат",
    "непросчитанных",
    "коллюдер",
    "монте-карло",
    "олл-ин",
    "тимпле",
    "быстрокнопк",
    "раскомментирова",
    "баттон",
    "регуляр",
    "многопотоково",
    "лимп",
    "баунд",
    "стат",
    "одномастн",
    "раздач",
    "патч",
)

fun inExtraDict(word: String) = extraDictionary.any {
    word.startsWith(it) && word.length - it.length < 4//5.coerceAtMost(word.length / 3)
}

fun allExts(root: File) = sequenceFiles(root)
    .map { f -> f.name.takeLastWhile { it != '.' } }.toList()

fun sequenceFiles(file: File, filter: (File) -> Boolean = { true }): Sequence<File> {
    return sequence {
        if (file.isDirectory) {
            for (local in file.listFiles() ?: arrayOf()) {
                yieldAll(sequenceFiles(local, filter))
            }
        } else if (filter.invoke(file))
            yield(file)
    }
}
