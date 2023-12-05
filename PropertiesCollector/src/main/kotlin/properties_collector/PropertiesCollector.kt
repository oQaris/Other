package properties_collector

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class PropertiesCollector(
    private val origProps: Path,
    private val exeFile: Path,
    private val isBackup: Boolean = false,
    private val isFullLog: Boolean = true,
    private val isFatMode: Boolean = false,
) {
    private val origWorkDir = exeFile.toAbsolutePath().parent.normalize()
    private val setProps = mutableSetOf<String>()

    fun run() {
        var firstStart = true
        while (true) {

            val curWorkDir = if (isBackup) cloneWorkDir(origWorkDir) else origWorkDir
            val curExeFile = curWorkDir.resolve(exeFile.fileName).normalize()
            val command = prepareCommand(curExeFile)
            if (firstStart) {
                println("$command in $curWorkDir")
                firstStart = false
            }

            val process = ProcessBuilder(command)
                .directory(curWorkDir.toFile())
                .redirectErrorStream(true)
                .start()

            var isUpdate = false
            for (line in process.inputStream.bufferedReader().lineSequence()) {
                if (isFullLog) println(line)

                val propStr = extractProperty(line) ?: continue
                if (setProps.add(propStr)) {
                    // Чтобы исключить множественное добавление необязательных свойств
                    addProperty(propStr)
                        .also { isUpdate = it }
                        // если успешно добавили, то завершаем процесс, но дочитываем из выходного потока
                        .takeIf { !it } ?: process.destroy()
                }
            }
            process.waitFor()
            if (isBackup) deleteDirectory(curWorkDir)

            if (!isUpdate) {
                if (process.exitValue() == 0)
                    println("Correct Work")
                else println("Another Exception")
                break
            }
        }
        println("Used properties:")
        println(setProps.joinToString("\n"))
    }

    private fun addProperty(name: String): Boolean {
        //todo не работает isFatMode
        fun Path.toDir() = let { if (isFatMode) it.parent else it }
        val dest = origWorkDir.resolve(Path.of(PROP_STR, name)).toDir()
        val src = origProps.resolve(name).toDir()
        if (!src.toFile().exists()) {
            // Если что-то не обязательное (типа кеша профилей)
            println("[?] " + src.toAbsolutePath())
            return false
        }
        println("[+] " + src.toAbsolutePath())
        Files.createDirectories(dest.parent)
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING)
        return true
    }
}
