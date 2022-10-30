package other.typo_finder

import analmess.inDictionary
import analmess.rusWords
import analmess.sortedCounter
import java.io.File

fun main() {
    // Какую директорию перебирать
    val root = File("Z:\\igas")

    //allExts(root).sortedCounter()
    //    .forEach { println(it.first + '\t' + it.second) }

    val allTypos = mutableListOf<String>()

    File("list_file_typos.txt").printWriter().apply {
        println("Search for typos in $root\nin files with extensions:\n" + exts.joinToString())

        sequenceFiles(root).forEach { file ->

            val typoWords = file.bufferedReader()
                .lineSequence().flatMap { line ->
                    line.rusWords().filterNot {
                        it.inDictionary() || inExtraDict(it)
                    }
                }.toList()

            allTypos += typoWords

            if (typoWords.isNotEmpty()) {
                println(file.absolutePath)
                typoWords.toSet().forEach { println(it) }
                println()
            }
        }
    }

    File("report_typos.txt").printWriter().apply {
        println("Total typos:  " + allTypos.size)
        println("Unique typos: " + allTypos.toSet().size)
        println("Sorted list of typos:")
        allTypos.sortedCounter().forEach { (k, v) ->
            println("$k\t$v")
        }
    }
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
    word.startsWith(it) && word.length - it.length < 5.coerceAtMost(word.length / 3)
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
