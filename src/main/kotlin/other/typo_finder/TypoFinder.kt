package other.typo_finder

import analmess.inDictionary
import analmess.rusWords
import java.io.File

fun main() {
    val listExts = listOf("txt", "kt", "java")
    val root = File("C:\\Users\\oQaris\\Desktop")

    sequenceFiles(root, listExts).forEach { file ->
        val typoWords = file.readText().rusWords()
            .filterNot { it.inDictionary() }
        if (typoWords.isNotEmpty()) {
            println(file.absolutePath)
            typoWords.forEach { println(it) }
            println()
        }
    }
}

fun sequenceFiles(f: File, listExts: List<String>): Sequence<File> {
    return sequence {
        if (f.isDirectory) {
            for (local in f.listFiles() ?: arrayOf()) {
                yieldAll(sequenceFiles(local, listExts))
            }
        }
        if (listExts.any { f.name.endsWith(".$it") })
            yield(f)
    }
}
