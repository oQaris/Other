package other.typo_finder

import analmess.inDictionary
import analmess.rusWords
import analmess.sortedCounter
import java.io.File

fun main() {
    // Какую директорию перебирать
    val root = File("C:\\Users\\oQaris\\Desktop\\Git")

    // Какие файлы просматривать
    val extsFilter = { f: File ->
        // с такими расширениями
        listOf("txt", "xml", "json", "kt", "kts", "java", "py")
            .any { f.name.endsWith(".$it") }
                // без такого в названии
                && listOf("values")
            .any { f.name.contains(it) }
    }

    val extraDictionary = setOf("рефакторинг", "токен")

    allExts(root).sortedCounter()
        .forEach { println(it.first + '\t' + it.second) }

    val allTypos = mutableListOf<String>()
    sequenceFiles(root, extsFilter).forEach { file ->

        val typoWords = file.bufferedReader()
            .lineSequence().flatMap { line ->
                line.rusWords().filterNot {
                    it.inDictionary() || it in extraDictionary
                }
            }.toList()

        allTypos += typoWords

        if (typoWords.isNotEmpty()) {
            println(file.absolutePath)
            typoWords.toSet().forEach { println(it) }
            println()
        }
    }

    println("Всего опечаток: " + allTypos.size)
    println("Уникальных:     " + allTypos.toSet().size)
    println("ТОП 100:")
    allTypos.sortedCounter().take(100).forEach { (k, v) ->
        println("$k\t$v")
    }
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
