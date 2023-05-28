package typo_finder

import java.io.File

class ExtraDictionary(src: File = File("data/extra_dictionary.txt")) {
    val extraDictionary = src.readLines()

    operator fun contains(word: String) = extraDictionary.any { dctWord ->
        //todo нормально определять окончания слов
        word.startsWith(dctWord) && word.length - dctWord.length < 4
    }
}
