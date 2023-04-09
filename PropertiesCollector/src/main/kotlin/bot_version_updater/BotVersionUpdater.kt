package bot_version_updater

import java.io.File

class BotVersionUpdater {
    private val gitModifiedFilesPattern = " M (.+)".toRegex()
    private val botCommitNotesPattern = "\\s*\\*/".toRegex()
    private val botVersionPattern = "public static final int \\w*BOT\\w*_VERSION = (\\d+);".toRegex()

    fun update(message: String) {
        preparedBotBaseClasses().forEach {
            incrementBotVersion(it, message)
        }
    }

    private fun preparedBotBaseClasses(): List<File> {
        return getModifiedFiles().filter {
            it.name.contains("Bot")
        }.map { file ->
            if (file.name.endsWith("Base.java")) file
            else searchBaseClass(file.name)
        }
    }

    private fun getModifiedFiles(): List<File> {
        val gitProcess = ProcessBuilder("git", "status", "-s").start()
        val output = gitProcess.inputStream.reader().readText()
        return gitModifiedFilesPattern.findAll(output).map {
            File(it.groups[1]!!.value)
        }.toList()
    }

    private fun searchBaseClass(botClassName: String) =
        combSequenceFiles("modules", "submodules") {
            it.name == botClassName.dropLast(5) + "Base.java"
        }.first()

    private fun incrementBotVersion(baseClass: File, message: String) {
        val text = StringBuilder(baseClass.bufferedReader().readText())

        val versionMatch = botVersionPattern.find(text)?.groups?.get(1)!!
        val newVersion = (versionMatch.value.toInt() + 1).toString()
        text.replace(versionMatch.range.first, versionMatch.range.last + 1, newVersion)

        val commentMatch = botCommitNotesPattern.find(text)!!
        text.insert(
            commentMatch.range.first,
            "\n * <h2>$newVersion<h2>\n * ${message.replace("\n", "\n * ")}"
        )

        baseClass.bufferedWriter().use { bw ->
            bw.write(text.toString())
        }
    }
}

fun main() {
    BotVersionUpdater().update("[*] Исправил альфы !BM-9999!")
    /*BotVersionUpdater().incrementBotVersion(
        File("C:\\Users\\oQaris\\Downloads\\36.txt"),
        "[&] ;aosidfkjasdhf;lg  askdk;l  !34567734!\n[^] sghsfdgh fghdf!!!"
    )*/
}
