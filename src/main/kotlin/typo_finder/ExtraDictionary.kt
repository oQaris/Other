package typo_finder

import java.io.File

class ExtraDictionary(src: File = File("extra_dictionary.txt")) {
    private val extraDictionary = src.readLines()

    operator fun contains(word: String) = extraDictionary.any {
        word.startsWith(it) && word.length - it.length < 4//5.coerceAtMost(word.length / 3)
    }
}
