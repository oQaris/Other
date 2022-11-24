package properties_collector

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.absolute
import kotlin.io.path.exists
import kotlin.io.path.name

class PropertiesCollector(
    private val origProps: Path,
    private val exeFile: Path,
    private val isBackup: Boolean
) {
    private val pattern = "properties([/\\\\][\\w-\\\\.]+)+".toRegex()
    private val origWorkDir = exeFile.absolute().parent.normalize()
    private val setProps = mutableSetOf<String>()

    fun run() {
        while (true) {
            val curWorkDir = if (isBackup) cloneWorkDir(origWorkDir) else origWorkDir
            val command = curWorkDir.resolve(exeFile.fileName).normalize().toString()
            println(command)

            val process = ProcessBuilder(command)
                .directory(curWorkDir.toFile())
                .redirectErrorStream(true)
                .start()

            var isUpdate = false
            for (line in process.inputStream.bufferedReader().lineSequence()) {
                val propStr = pattern.find(line)?.value ?: continue

                if (setProps.add(propStr)) {
                    addProperty(propStr)
                        .also { isUpdate = it }
                        .takeIf { !it } ?: break // если успешно добавили, то перезапускаем
                }
            }
            process.destroyForcibly()
            process.waitFor()

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
            newPath = workDir.parent.resolve("${workDir.name}_$i")
            i++
            if (!newPath.exists())
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
        if (!src.exists()) {
            println("[?] " + src.absolute())
            return false
        }
        println("[+] " + src.absolute())
        Files.createDirectories(dest.parent)
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING)
        return true
    }
}
