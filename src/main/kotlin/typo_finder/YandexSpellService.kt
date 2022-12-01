package typo_finder

import ru.amayakasa.linguistic.YandexSpeller
import ru.amayakasa.linguistic.parameters.Language
import ru.amayakasa.linguistic.parameters.ResponseInterface
import ru.amayakasa.linguistic.parameters.Version
import java.io.File

class YandexSpellService(cacheFile: File = File("yandex_cache.csv")) {
    private val speller = YandexSpeller(Version.SPELLER_LATEST, ResponseInterface.SPELLER_JSON)
    private val cacheTrue = cacheFile.readLines()
        .map { it.split(';').first() }

    fun toCorrect(input: String): String {
        if (input in cacheTrue)
            return input

        var process = input
        var shift = 0
        speller.getSpelledPhrase(input, Language.RUSSIAN)
            .misspelledWords.forEach {
                val corrected = it.variants.first()
                val start = shift + it.position
                process = process.replaceRange(
                    start until start + it.length,
                    corrected
                )
                shift += corrected.length - it.length
            }
        return process
    }

    fun isCorrect(input: String): Boolean {
        return toCorrect(input) == input
    }
}

fun main() {
    val spellService = YandexSpellService()
    val extraDictionary = ExtraDictionary()

    extraDictionary.extraDictionary
        .filter { !spellService.isCorrect(it) }
        .sorted()
        .forEach { println(it) }
}
