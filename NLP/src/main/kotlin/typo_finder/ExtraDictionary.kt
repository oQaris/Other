package typo_finder

import java.io.File

class ExtraDictionary(src: File = File("extra_dictionary.txt")) {
    val extraDictionary = src.readLines()

    operator fun contains(word: String) = extraDictionary.any { dctWord ->
        word.startsWith(dctWord) && word.length - dctWord.length < 4//5.coerceAtMost(word.length / 3)
    }
}
