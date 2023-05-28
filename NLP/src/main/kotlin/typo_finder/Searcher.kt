package typo_finder

import en
import ru
import rusWords
import processors.sortedCounter
import findBy
import java.io.File

// Просто поиск интересных штук в проекте
fun main() {
    searchBy(::enRusWords)
}

fun customRegex(file: File): List<String> {
    return "\\S*счит\\S*".toRegex()
        .findAll(file.readText())
        .flatMap { it.value.rusWords() }
        .toList()
}

fun enRusWords(file: File): List<String> {
    return file.readText()
        .findBy("(?i:[a-za-яё])+".toRegex())
        .filter { word ->
            word.contains(ru.toRegex())
                    && word.contains(en.toRegex())
        }.toList()
}

fun searchBy(handler: (File) -> (List<String>)) {
    val results = mutableListOf<String>()
    sequenceFiles(File("Z:\\igas"), extsFilter)
        .forEach { file ->
            results += handler.invoke(file)
        }
    results.sortedCounter()
        .forEach { println(it.first) }
}

fun searchDuplicateClass() {
    val results = mutableListOf<String>()
    sequenceFiles(File("Z:\\igas")) {
        it.name.endsWith("java")
    }.forEach { file ->
        val className = file.name.dropLast(5)
        results += className
    }
    results.sortedCounter()
        .filter { it.second >= 2 }
        .forEach { println(it.first + '\t' + it.second) }
}
