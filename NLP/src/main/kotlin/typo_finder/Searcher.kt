package typo_finder

import en
import findBy
import processors.sortedCounter
import ru
import rusWords
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

// Просто поиск интересных штук в проекте
fun main() {
    wizardFilesMerger()
}

fun imageExtract() {
    val images = combSequenceFiles(
        listOf(
            "\\\\10.10.250.81\\Oqaris_z\\NLT28Neuro\\AutoNeuroClientLateStage",
            "\\\\10.10.250.68\\Oqaris\\NLT28Neuro\\AutoNeuroClientLateStage",
            "\\\\10.10.250.63\\Oqaris_z\\NLT28Neuro\\AutoNeuroClientLateStage"
        )
    ).filter { !it.name.contains('.') && it.name.contains("MergeSettingsCheck") }

    val target = Path.of("\\\\10.10.250.81\\Oqaris_z\\NLT28Neuro\\AutoNeuroServerLateStage\\ImageGather\\IMAGES\\out")
    val uniqueImgs = images.groupBy { it.name }.map { it.value.first() }
    uniqueImgs.forEach { img ->
        Files.move(img.toPath(), target.resolve(img.name))
    }
}

fun wizardFilesMerger() {
    val target = Path.of("D:\\GtoWizard\\no_ante")
    val charts = sequenceFiles(File("D:\\GtoWizard\\raw"))
    charts.groupBy { it.name.substring(it.name.split("_")[0].length) }.forEach { pair ->
        val needFile = if (pair.value.size == 1) {
            pair.value.single()
        } else if (pair.value.size == 2) {
            pair.value.single { it.name.contains("HuSngTest") }
        } else throw IllegalStateException()

        Files.move(needFile.toPath(), target.resolve(needFile.name))
    }
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
    sequenceFiles(File("Z:\\igas"), extsFilter(projExts))
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

fun igasModulesGradleReplacer() {
    val settings = File("Z:\\igas\\settings.gradle.kts")
    val regex =
        "include\\(\"(.*)\"\\)\\r\\nproject\\(\":\\1\"\\)\\.projectDir =([\\n\\s])+file\\(\"((sub)?modules/(.*))\"\\)"
            .toRegex(RegexOption.MULTILINE)

    var text = settings.readText()
    // true - можно использовать includeProject()
    // false - только includeAndSetupProject()
    val matchers = regex.findAll(text).map { res ->
        val projectPath = res.groups[1]!!.value
        val pathDir = res.groups.last()!!.value
        res to (projectPath == pathDir.replace('/', ':'))
    }.toList()

    // проверка, что модули не дублируются
    require(matchers.size == matchers.map { it.first.value }.toSet().size)

    matchers.forEach { matchEntry ->
        val match = matchEntry.first
        val projectDir = match.groups[match.groups.size - 3]!!.value
        text = if (matchEntry.second) {
            text.replace(match.value, "includeProject(\"$projectDir\")")
        } else {
            text.replace(match.value, "includeAndSetupProject(\"${match.groups[1]!!.value}\", \"$projectDir\")")
        }
    }
    println(text)
}
