package typo_finder

import ru.amayakasa.linguistic.YandexSpeller
import ru.amayakasa.linguistic.parameters.Language
import ru.amayakasa.linguistic.parameters.ResponseInterface
import ru.amayakasa.linguistic.parameters.Version
import java.io.File

class YandexSpellService(cacheFile: File = File("yandex_cache.csv")) {

    val cacheTrue = cacheFile.readLines()
        .map { it.split(';').first() }

    fun toCorrect(input: String): String {
        require(!input.contains(" ")) { "Пока доступно только исправление одного слова" }

        //todo добавить исправление всего предложения, а не первого слова
        val correct = YandexSpeller(Version.SPELLER_LATEST, ResponseInterface.SPELLER_JSON)
            .getSpelledPhrase(input, Language.RUSSIAN).misspelledWords.firstOrNull()?.variants?.first() ?: input

        return correct
    }
}

fun main() {
    val yandexSpellService = YandexSpellService()

    val extraDictionary = ExtraDictionary()

    extraDictionary.extraDictionary.forEach {

    }
}