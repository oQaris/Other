package bot_version_updater

import java.io.File
import kotlin.system.exitProcess

/**
 * test
 * <h2>368<h2>
 * 23452345
 */
class BotVersionUpdater {
    private val gitModifiedFilesPattern = "\\s*M\\s+(.+)".toRegex()
    private val gitDiffLinesPattern = "[+-]\\s+(.+)".toRegex()
    private val botCommitNotesPattern = "\\s*\\*/".toRegex()
    private val botVersionPattern = "public static final int \\w*BOT\\w*_VERSION = (\\d+);".toRegex()
    private val test = "public static final int BOT_VERSION = 368;"

    fun update(message: String) {
        val botClasses = getNecessaryFiles()
        if (botClasses.isEmpty()) {
            println("No bot classes")
            return
        }
        println("Bots: " + botClasses.joinToString(", ") { it.name })
        println("New versions: " +
                botClasses.joinToString(", ") {
                    incrementBotVersion(it, message)
                }
        )
        println("Commit note: $message")
    }

    private fun getNecessaryFiles(): List<File> {
        return preparedBotBaseClasses().filter { file ->
            val gitDiffProcess = ProcessBuilder("git", "diff", file.absolutePath).start()
            val output = gitDiffProcess.inputStream.reader().readText()
            println(output)
            gitDiffLinesPattern.findAll(output).all { diffLine ->
                botVersionPattern.find(diffLine.groups[1]!!.value) == null
            }
        }
    }

    private fun preparedBotBaseClasses(): List<File> {
        return getModifiedFiles().filter {
            it.name.contains("Bot")
        }/*.map { file ->
            if (file.name.endsWith("Base.java")) file
            else searchBaseClass(file.name)
        }*/
    }

    private fun getModifiedFiles(): List<File> {
        val gitStatusProcess = ProcessBuilder("git", "status", "-s").start()
        val output = gitStatusProcess.inputStream.reader().readText()
        println(output)
        return gitModifiedFilesPattern.findAll(output).map {
            File(it.groups[1]!!.value)
        }.toList()
    }

    private fun searchBaseClass(botClassName: String) =
        combSequenceFiles("modules", "submodules") {
            it.name == botClassName.dropLast(5) + "Base.java"
        }.first()

    private fun incrementBotVersion(baseClass: File, message: String): String {
        try {
            val text = StringBuilder(baseClass.bufferedReader().readText())

            val versionMatch = botVersionPattern.find(text)!!.groups[1]!!
            val newVersion = (versionMatch.value.toInt() + 1).toString()
            text.replace(versionMatch.range.first, versionMatch.range.last + 1, newVersion)

            val commentMatch = botCommitNotesPattern.find(text)!!
            text.insert(
                commentMatch.range.first,
                "\n * <h2>$newVersion<h2>\n * ${message.replace("\n", "\n * ")}"
            )

            baseClass.bufferedWriter().use { bw ->
                bw.write(text.toString())
                bw.flush()
            }
            return newVersion
        } catch (e: Exception) {
            return "fail"
        }
    }
}

fun main(args: Array<String>) {
    try {
        val message = File(".git\\COMMIT_EDITMSG").readText().trim()
        BotVersionUpdater().update(message)
        Thread.sleep(1000)
    } catch (e: Exception) {
        e.printStackTrace()
        exitProcess(1)
    }
    exitProcess(0)
}
