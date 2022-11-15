package other.typo_finder

import analmess.inDictionary
import analmess.rusWords
import analmess.sortedCounter
import ru.amayakasa.linguistic.YandexSpeller
import ru.amayakasa.linguistic.parameters.Language
import ru.amayakasa.linguistic.parameters.ResponseInterface
import ru.amayakasa.linguistic.parameters.Version
import java.io.File
import java.util.concurrent.TimeUnit

fun main() {
    // Какую директорию перебирать
    val root = File("Z:\\igas\\modules\\gameinfo")

    //allExts(root).sortedCounter()
    //    .forEach { println(it.first + '\t' + it.second) }

    var timeMark = System.currentTimeMillis()
    fun getDurationSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeMark)
        .also { timeMark = System.currentTimeMillis() }

    val allTypos = mutableListOf<String>()

    val chatter1 = ProgressChatter {
        println("Обработано $it файлов. Найдено ${allTypos.size} неизвестных слов.")
    }
    println("Запущен поиск комментариев...")

    File("list_file_typos.txt").printWriter().apply {
        println("Search for typos in $root\nin files with extensions:\n" + exts.joinToString())

        sequenceFiles(root, extsFilter).forEach { file ->
            val typoWords = file.bufferedReader()
                .lineSequence().flatMap { line ->
                    line.rusWords().filterNot {
                        inExtraDict(it) || it.inDictionary()
                    }
                }.toList()

            /*val comments = file.readText()
                .comments(CommentRegex.JAVA)
                .map { it.trim() }
                .map { it.replace("(\\s+\\*\\s+)|\\s+".toRegex(), " ") }

            val rusTypoComments = comments.filter { com ->
                com.rusWords().any { !it.inDictionary() }
            }*/

            allTypos += typoWords

            if (typoWords.isNotEmpty()) {
                println(file.absolutePath)
                typoWords.toSet().forEach { println(it) }
                println()
            }
            chatter1.incProgress()
        }
        chatter1.chatProgress()
    }.flush()

    println("Все файлы обработаны за ${getDurationSec()} секунд.\nЗапущена доп.проверка опечаток с помощью Yandex.Speller...")

    var confirmed = 0
    val chatter2 = ProgressChatter(10)
    File("report_typos.csv").printWriter().apply {
        println("Total typos:;" + allTypos.size + ';')
        println("Unique typos:;" + allTypos.toSet().size + ';')
        println("Sorted list of typos:;;")
        try {
            allTypos.sortedCounter().forEach { (word, count) ->
                //todo добавить исправление всего предложения, а не первого слова
                val correct = YandexSpeller(Version.SPELLER_LATEST, ResponseInterface.SPELLER_JSON)
                    .getSpelledPhrase(word, Language.RUSSIAN).misspelledWords.firstOrNull()?.variants?.first() ?: ""
                if (correct.isNotEmpty()) {
                    println("$word;$correct;$count")
                    confirmed++
                }
                chatter2.incProgress("Обработано $ неизвестных слов. Найдено $confirmed потенциальных опечаток.")
            }
        } catch (e: Exception) {
            flush()
        }
        println()
    }.flush()
    println("Все найденные опечатки обработаны за ${getDurationSec()} секунд.\nПодтверждённых - $confirmed")
}

val exts = setOf(
    "txt",
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
    "json",
    "kt",
    "kts",
    "java",
    "py"
)

// Какие файлы просматривать
val extsFilter = { f: File ->
    // с такими расширениями
    exts.any { f.name.endsWith(".$it") }
    // без такого в названии
    //&& setOf("values")
    //.any { f.name.contains(it) }
}

val extraDictionary = File("extra_dictionary.txt")
    .readLines().toSet().map { it.trim() }.filter { it.isNotBlank() }

fun inExtraDict(word: String) = extraDictionary.any {
    word.startsWith(it) && word.length - it.length < 3
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
