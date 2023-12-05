package typo_finder

import java.io.File

val projExts = setOf(
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

fun extsFilter(exts: Set<String>): (File) -> Boolean {
    return { f -> exts.any { f.name.endsWith(".$it") } }
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

fun combSequenceFiles(roots: List<String>, filter: (File) -> Boolean = { true }): Sequence<File> {
    return roots.map { sequenceFiles(File(it), filter) }
        .reduce { acc, sequence -> acc + sequence }
}
