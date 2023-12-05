package properties_collector

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.name

const val PROP_STR = "properties"

//val PROP_ROOT: Path = Path.of("$PROP_STR/")
//val PROP_PATTERN = ".*?(?:$PROP_STR)?(([/\\\\][\\w-\\\\.]+)+)".toRegex()
val PATH_PATTERN = "([\\w-\\\\.]+[/\\\\])+[\\w-\\\\.]+".toRegex()
val PROP_PATTERN = ".*Exception:.*$PATH_PATTERN.*".toRegex()

fun extractProperty(line: String): String? {
    if (!PROP_PATTERN.matches(line)) return null
    return extractPaths(line)
        .map { Path.of(it).normalize() }
        .map {
            if (it.getName(0).name == PROP_STR)
                it.subpath(1, it.nameCount)
            else it
        }.map { it.toString() }
        .distinct()
        .nullOrSingle()
}

private fun extractPaths(line: String): List<String> {
    return PATH_PATTERN.findAll(line).toList().map { it.value }
}

fun <T> List<T>.nullOrSingle(): T? {
    return when (size) {
        0 -> null
        1 -> this[0]
        else -> null
    }
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
