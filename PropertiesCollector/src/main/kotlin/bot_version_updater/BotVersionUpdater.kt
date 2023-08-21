package bot_version_updater

import java.io.File

/**
 * test description
 * <h2>369<h2>
 * [+] test message
 */
class BotVersionUpdater {
    private val gitModifiedFilesPattern = "\\s*M\\s+(.+)".toRegex()
    private val gitDiffLinesPattern = "[+-]\\s+(.+)".toRegex()
    private val botCommitNotesPattern = "\\s*\\*/".toRegex()
    private val botVersionPattern = "public static final int \\w*BOT\\w*_VERSION = (\\d+);".toRegex()
    private val test = "public static final int BOT_VERSION = 369;"

    fun update(message: String) {
        val botClasses = getFilesWithUnmodifiedVersions()
        if (botClasses.isEmpty()) {
            println("No changed bot classes")
            return
        }
        val commitNote = message.ifBlank { extractCommitNote() }
        println("Commit note: $commitNote")

        val resultBotToVersion = botClasses.map { it.name }.zip(
            botClasses.map {
                try {
                    incrementBotVersion(it, commitNote)
                } catch (e: Exception) {
                    "fail!"
                }
            }
        )
        println("New versions:\n" + resultBotToVersion
            .joinToString("\n") {
                it.first + " -> " + it.second
            }
        )
    }

    private fun extractCommitNote(): String {
        val gitLogProcess = ProcessBuilder("git", "log", "-1", "--pretty=format:%s").start()
        val origMsg = gitLogProcess.inputStream.reader().readText()
        return try {
            origMsg.split("\n").first()
                .split("-").let {
                    it[0].trim().take(3) + " " + it.drop(1).joinToString("-").trim()
                }
        } catch (e: Exception) {
            origMsg // Если сообщение коммита не соответствует формату
        }
    }

    /**
     * Заменяет текст в файле [baseClass], увеличивая значение переменной версии на 1
     * и добавляя в документацию заметку к версии [message].
     * @return стоку со значением новой версии
     */
    private fun incrementBotVersion(baseClass: File, message: String): String {
        val textCode = StringBuilder(baseClass.bufferedReader().readText())

        val versionMatch = botVersionPattern.find(textCode)!!.groups[1]!!
        val newVersion = (versionMatch.value.toInt() + 1).toString()
        textCode.replace(versionMatch.range.first, versionMatch.range.last + 1, newVersion)

        val commentMatch = botCommitNotesPattern.find(textCode)!!
        textCode.insert(
            commentMatch.range.first,
            "\n * <h2>$newVersion<h2>\n * ${message.replace("\n", "\n * ")}"
        )

        baseClass.bufferedWriter().use { bw ->
            bw.write(textCode.toString())
            bw.flush()
        }
        return newVersion
    }

    private fun getFilesWithUnmodifiedVersions(): List<File> {
        return preparedBotBaseClasses().filter { file ->
            val gitDiffProcess = ProcessBuilder("git", "diff", file.absolutePath).start()
            val gitShowProcess = ProcessBuilder("git", "show", "HEAD", "--", file.absolutePath).start()
            val output = gitDiffProcess.inputStream.reader().readText() +
                    "\n" + gitShowProcess.inputStream.reader().readText()
            gitDiffLinesPattern.findAll(output).all { diffLine ->
                botVersionPattern.find(diffLine.groups[1]!!.value) == null
            }
        }
    }

    private fun preparedBotBaseClasses(): List<File> {
        return getModifiedFilesLastCommit().filter {
            it.name.contains("Bot")
        }.map { file ->
            searchBaseClassIfNeed(file)
        }
    }

    private fun getModifiedFilesLastCommit(): List<File> {
        val allRepos = listOf(File(".")) + (File("submodules").listFiles()?.toList() ?: listOf<File>())
        return buildList {
            allRepos.forEach { dir ->
                val gitLogProcess = ProcessBuilder("git", "log", "--oneline", "origin/master..HEAD")
                    .directory(dir)
                    .start()
                val countNewCommits = gitLogProcess.inputStream.reader().readLines().count()
                if (countNewCommits > 0) {
                    val gitDiffProcess =
                        ProcessBuilder("git", "diff", "--name-status", "HEAD~$countNewCommits", "HEAD")
                            .directory(dir)
                            .start()
                    val output = gitDiffProcess.inputStream.reader().readText()
                    addAll(gitModifiedFilesPattern.findAll(output).map {
                        File(it.groups[1]!!.value)
                    }.toList())
                }
            }
        }
    }

    private fun searchBaseClassIfNeed(botClass: File): File {
        val baseSuffix = "Base.java"
        return if (botClass.name.endsWith(baseSuffix)) botClass
        else {
            combSequenceFiles("modules") {
                it.name == botClass.name.dropLast(5) + baseSuffix
            }.firstOrNull() ?: botClass
        }
    }
}

/**
 * args[0] - единственная строка с примечанием к версии. Если содержит пробелы, то необходимо взять в кавычки.
 * Может быть пустой, тогда в качестве примечания берётся информация из сообщения к последнему коммиту.
 */
fun main(args: Array<String>) {
    try {
        BotVersionUpdater().update(if (args.isEmpty()) "" else args.single())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
