package properties_collector

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

const val PROP_STR = "properties"

fun extractProperty(line: String): String? {
    //todo слово properties может отсутствовать, но обязательно есть "Exception:"
    val pattern = ".*Exception: (?:$PROP_STR)?(([/\\\\][\\w-\\\\.]+)+)".toRegex()
    return pattern.matchEntire(line)?.groups?.get(1)?.value
}

fun cloneWorkDir(workDir: Path): Path {
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

fun deleteDirectory(directory: Path) {
    Files.walk(directory)
        .sorted(Comparator.reverseOrder())
        .map { it.toFile() }
        .forEach { it.delete() }
}

fun prepareCommand(curExeFile: Path): List<String> {
    if (curExeFile.fileName.toString().endsWith(".jar"))
        return listOf("java", "-jar", curExeFile.toString())
    /*if (curExeFile.name.endsWith(".txt"))
        return curExeFile.bufferedReader().readLine()
            .split("\\s".toRegex()).filter { it.isNotBlank() }*/
    return listOf(curExeFile.toString())
}
