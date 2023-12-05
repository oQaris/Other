package typo_finder

import inDictionary
import processors.sortedCounter
import rusWords
import java.io.File
import java.util.concurrent.TimeUnit

fun main() {
    // Какую директорию перебирать
    val roots = listOf("Z:\\ml")
    // Название выходного файла в папке logs
    val report = "ml2.csv"
    // Расширения имён файлов, в которых надо искать опечатки (если пустое - ищется во всех)
    val ext = setOf<String>()

    var timeMark = System.currentTimeMillis()
    fun getDurationSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeMark)
        .also { timeMark = System.currentTimeMillis() }

    println("Запущен поиск комментариев...")
    val allTypos = buildTyposWithFreqQueue(roots, ExtraDictionary(), ext)

    println("Все файлы обработаны за ${getDurationSec()} секунд.\nЗапущена доп.проверка опечаток с помощью Yandex.Speller...")

    var confirmed = 0
    val chatter = ProgressChatter(35)
    val speller = YandexSpellService()
    File("logs/$report").printWriter().apply {
        println("Total typos:;" + allTypos.size + ';')
        println("Unique typos:;" + allTypos.toSet().size + ';')
        println("Sorted list of typos:;;")
        try {
            allTypos.sortedCounter().forEach { (word, count) ->
                val correct = speller.toCorrect(word)
                if (correct != word) {
                    println("$word;${correct};$count")
                    confirmed++
                }
                chatter.incProgress("Обработано $ неизвестных слов. Найдено $confirmed потенциальных опечаток.")
            }
        } catch (e: Exception) {
            println(e)
            flush()
        }
        println()
    }.flush()
    println("Сохранение кеша...")
    speller.saveCache()
    println("Все найденные опечатки обработаны за ${getDurationSec()} секунд.\nПодтверждённых - $confirmed")
}

fun buildTyposWithFreqQueue(roots: List<String>, extraDictionary: ExtraDictionary, exts: Set<String>): List<String> {
    val allTypos = mutableListOf<String>()
    val chatter = ProgressChatter(500) {
        println("Обработано $it файлов. Найдено ${allTypos.size} неизвестных слов.")
    }

    println("Search for typos in ${roots.joinToString()}\nin files with extensions:\n" + exts.joinToString())
    File("list_file_typos.txt").printWriter().apply {

        combSequenceFiles(roots, extsFilter(exts)).forEach { file ->
            val typoWords = file.bufferedReader()
                .lineSequence().flatMap { line ->
                    line.rusWords().filterNot {
                        it in extraDictionary || it.inDictionary()
                    }
                    /*line.rusSentences().filterNot { snt ->
                        snt.tokens().all { it in extraDictionary || it.inDictionary() }
                    }*/
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
            chatter.incProgress()
        }
        chatter.chatProgress()
    }.flush()
    return allTypos
}
