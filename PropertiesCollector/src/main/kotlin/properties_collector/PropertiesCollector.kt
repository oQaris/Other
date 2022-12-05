package properties_collector

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class PropertiesCollector(
    private val origProps: Path,
    private val exeFile: Path,
    private val isBackup: Boolean = false,
    private val isFullLog: Boolean = true
) {
    private val pattern = "properties([/\\\\][\\w-\\\\.]+)+".toRegex()
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

                val propStr = pattern.find(line)?.value ?: continue
                if (setProps.add(propStr)) {
                    addProperty(propStr)
                        .also { isUpdate = it }
                        // если успешно добавили, то завершаем процесс, но дочитываем из выходного потока
                        .takeIf { !it } ?: process.destroy()
                }
            }
            process.waitFor()
            if (isBackup)
                deleteDirectory(curWorkDir)

            if (!isUpdate) {
                if (process.exitValue() == 0)
                    println("Correct Work")
                else println("Another Exception")
                return
            }
        }
    }

    private fun cloneWorkDir(workDir: Path): Path {
        var newPath: Path
        var i = 0
        while (true) {
            newPath = workDir.parent.resolve("${workDir.fileName}_$i")
            i++
            if (!newPath.toFile().exists())
                break
        }
        Files.walk(workDir).forEach {
            Files.copy(
                it, newPath.resolve(workDir.relativize(it)),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
        return newPath
    }

    private fun addProperty(name: String): Boolean {
        val dest = origWorkDir.resolve(name)
        val src = origProps.parent.resolve(name)
        if (!src.toFile().exists()) {
            println("[?] " + src.toAbsolutePath())
            return false
        }
        println("[+] " + src.toAbsolutePath())
        Files.createDirectories(dest.parent)
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING)
        return true
    }

    private fun deleteDirectory(directory: Path) {
        Files.walk(directory)
            .sorted(Comparator.reverseOrder())
            .map { it.toFile() }
            .forEach { it.delete() }
    }

    private fun prepareCommand(curExeFile: Path): List<String> {
        if (curExeFile.fileName.toString().endsWith(".jar"))
            return listOf("java", "-jar", curExeFile.toString())
        /*if (curExeFile.name.endsWith(".txt"))
            return curExeFile.bufferedReader().readLine()
                .split("\\s".toRegex()).filter { it.isNotBlank() }*/
        return listOf(curExeFile.toString())
    }
}
