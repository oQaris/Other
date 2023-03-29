package typo_finder

import inDictionary
import processors.sortedCounter
import rusWords
import java.io.File
import java.util.concurrent.TimeUnit

fun main() {
    // Какую директорию перебирать
    val roots = listOf("Z:\\igas\\submodules\\private_cash_holdem\\green2_private", "Z:\\igas\\modules\\CaseCollector\\src\\main\\java\\com\\gmware\\applications\\casecollector\\profilesaver")

    //allExts(root).processors.sortedCounter()
    //    .forEach { println(it.first + '\t' + it.second) }

    var timeMark = System.currentTimeMillis()
    fun getDurationSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeMark)
        .also { timeMark = System.currentTimeMillis() }

    println("Запущен поиск комментариев...")
    val allTypos = buildTyposWithFreqQueue(roots, ExtraDictionary())

    println("Все файлы обработаны за ${getDurationSec()} секунд.\nЗапущена доп.проверка опечаток с помощью Yandex.Speller...")

    var confirmed = 0
    val chatter2 = ProgressChatter(33)
    val speller = YandexSpellService()
    File("test.csv").printWriter().apply {
        //println("Total typos:;" + allTypos.size + ';')
        //println("Unique typos:;" + allTypos.toSet().size + ';')
        //println("Sorted list of typos:;;")
        try {
            allTypos.sortedCounter().forEach { (word, count) ->
                val correct = speller.toCorrect(word)
                if (correct != word) {
                    println("$word;${correct};$count")
                    confirmed++
                }
                chatter2.incProgress("Обработано $ неизвестных слов. Найдено $confirmed потенциальных опечаток.")
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

fun buildTyposWithFreqQueue(roots: List<String>, extraDictionary: ExtraDictionary): List<String> {
    val allTypos = mutableListOf<String>()
    val chatter = ProgressChatter {
        println("Обработано $it файлов. Найдено ${allTypos.size} неизвестных слов.")
    }

    File("list_file_typos.txt").printWriter().apply {
        println("Search for typos in ${roots.joinToString()}\nin files with extensions:\n" + exts.joinToString())

        combSequenceFiles(roots).forEach { file ->
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
