package typo_finder

import analmess.*
import java.io.File
import java.util.concurrent.TimeUnit

fun main() {
    // Какую директорию перебирать
    val root = File("Z:\\igas\\modules\\profiles")

    //allExts(root).sortedCounter()
    //    .forEach { println(it.first + '\t' + it.second) }

    var timeMark = System.currentTimeMillis()
    fun getDurationSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeMark)
        .also { timeMark = System.currentTimeMillis() }

    val allTypos = mutableListOf<String>()

    val extraDictionary = ExtraDictionary()
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
            chatter1.incProgress()
        }
        chatter1.chatProgress()
    }.flush()

    println("Все файлы обработаны за ${getDurationSec()} секунд.\nЗапущена доп.проверка опечаток с помощью Yandex.Speller...")

    var confirmed = 0
    val chatter2 = ProgressChatter(10)
    val speller = YandexSpellService()
    File("new.csv").printWriter().apply {
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
    println("Все найденные опечатки обработаны за ${getDurationSec()} секунд.\nПодтверждённых - $confirmed")
}
