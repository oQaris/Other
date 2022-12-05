package typo_finder

import rusWords
import sortedCounter
import java.io.File

fun main() {
    searchDuplicateClass()
}

fun searchByRegex() {
    val results = mutableListOf<String>()
    sequenceFiles(File("Z:\\igas"), extsFilter).forEach { file ->
        results += "\\S*счит\\S*".toRegex()
            .findAll(file.readText())
            .flatMap { it.value.rusWords() }
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
