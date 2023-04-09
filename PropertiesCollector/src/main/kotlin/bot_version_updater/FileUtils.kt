package bot_version_updater

import java.io.File

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

fun combSequenceFiles(vararg roots: String, filter: (File) -> Boolean = { true }): Sequence<File> {
    return roots.map { sequenceFiles(File(it), filter) }
        .reduce { acc, sequence -> acc + sequence }
}
