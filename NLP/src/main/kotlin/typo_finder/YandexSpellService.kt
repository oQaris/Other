package typo_finder

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import ru.amayakasa.linguistic.YandexSpeller
import ru.amayakasa.linguistic.parameters.Language
import ru.amayakasa.linguistic.parameters.ResponseInterface
import ru.amayakasa.linguistic.parameters.Version
import ru.amayakasa.linguistic.response.Phrase
import java.io.File

class YandexSpellService(cacheFile: File = File("yandex_cache.json")) {
    private val speller = YandexSpeller(Version.SPELLER_LATEST, ResponseInterface.SPELLER_JSON)
    private val cache = YandexSpellerCache(cacheFile)

    fun toCorrect(input: String): String {

        require(input.length < 10_000)

        return cache.data[input] ?: applyPatch(
            input,
            speller.getSpelledPhrase(input, Language.RUSSIAN)
        )
    }

    fun toCorrect(input: List<String>): List<String> {

        //todo применять частично
        if (input.all { it in cache.data.keys })
            return input.map { cache.data[it]!! }

        return input.toList().chunked(100).flatMap {
            val local = it.toTypedArray()
            speller.getSpelledPhrases(local, Language.RUSSIAN)
                .mapIndexed { i, patch ->
                    applyPatch(local[i], patch)
                }
        }
    }

    private fun applyPatch(input: String, patch: Phrase): String {
        var process = input
        var shift = 0
        patch.misspelledWords.forEach {
            val corrected = it.variants.first()
            val start = shift + it.position
            process = process.replaceRange(
                start until start + it.length,
                corrected
            )
            shift += corrected.length - it.length
        }
        cache.data[input] = process
        return process
    }

    fun isCorrect(input: String): Boolean {
        return toCorrect(input) == input
    }

    /**
     * Число ошибочных фраз (<= input.size), даже если в одной фразе больше 1 ошибки
     */
    fun countMisspelled(input: List<String>): Int {
        return toCorrect(input).zip(input)
            .count { it.first == it.second }
    }

    fun saveCache() {
        cache.save()
    }
}


@OptIn(ExperimentalSerializationApi::class)
class YandexSpellerCache(private val file: File) {
    val data: MutableMap<String, String> =
        if (file.exists())
            Json.decodeFromStream(file.inputStream())
        else mutableMapOf()

    fun save() {
        Json.encodeToStream(data, file.outputStream())
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
