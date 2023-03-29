package typo_finder

import java.io.File

val exts = setOf(
    "txt",
    "MF",
    "md",
    "toml",
    "properties",
    "config",
    "yml",
    "iml",
    "gitignore",
    "csv",
    "xml",
    "json",
    "xml",
    "proto",
    "demo",
    "py",
    "kt",
    "kts",
    "java",
)

// Какие файлы просматривать
val extsFilter = { f: File ->
    // с такими расширениями
    exts.any { f.name.endsWith(".$it") }
    //true
    // без такого в названии
    //&& setOf("values")
    //.any { f.name.contains(it) }
}

fun allExts(root: File) = sequenceFiles(root)
    .map { f -> f.name.takeLastWhile { it != '.' } }.toList()

fun sequenceFiles(file: File, filter: (File) -> Boolean = { true }): Sequence<File> {
    return sequence {
        if (file.isDirectory) {
            for (local in file.listFiles() ?: arrayOf()) {
                yieldAll(sequenceFiles(local, filter))
            }
        } else if (filter.invoke(file))
            yield(file)
    }
}

fun combSequenceFiles(roots: List<String>): Sequence<File> {
    return roots.map { sequenceFiles(File(it), extsFilter) }
        .reduce { acc, sequence -> acc + sequence }
}
