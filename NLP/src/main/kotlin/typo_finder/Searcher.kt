package typo_finder

import en
import findBy
import processors.sortedCounter
import ru
import rusWords
import java.io.File

// Просто поиск интересных штук в проекте
fun main() {
    searchBy(::botVersion)
}

fun customRegex(file: File): List<String> {
    return "\\S*счит\\S*".toRegex()
        .findAll(file.readText())
        .flatMap { it.value.rusWords() }
        .toList()
}

fun latinC(file: File): List<String> {
    return file.readText()
        .findBy("($ru+ c )|( c $ru+)|($ru+c$ru*)|($ru*c$ru+)".toRegex())
        .toList()
}

fun botVersion(file: File): List<String> {
    val patternDescription = "Версия робота, единая для оппонента и экспертных наследников".toRegex()
    val patternVersion = "public static final int \\w+_BOT_VERSION = \\d+;".toRegex()
    val text = file.readText()
    val wasDetect = text.contains(patternDescription) xor text.contains(patternVersion)
    return if (wasDetect) listOf(file.name) else listOf()
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
