package typo_finder

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import ru.amayakasa.linguistic.YandexSpeller
import ru.amayakasa.linguistic.parameters.Language
import ru.amayakasa.linguistic.parameters.ResponseInterface
import ru.amayakasa.linguistic.parameters.Version
import ru.amayakasa.linguistic.response.Phrase
import java.io.File

class YandexSpellService {
    private val speller = YandexSpeller(Version.SPELLER_LATEST, ResponseInterface.SPELLER_JSON)
    private val cache = YandexSpellerCache()

    fun toCorrect(input: String): String {

        require(input.length < 10_000)

        return cache[input] ?: applyPatch(
            input,
            speller.getSpelledPhrase(input, Language.RUSSIAN)
        )
    }

    fun toCorrect(input: List<String>): List<String> {

        //todo применять частично
        if (input.all { it in cache.keys })
            return input.map { cache[it]!! }

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
        if (input != process)
            cache[input] = process
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

@Serializable
@OptIn(ExperimentalSerializationApi::class)
class YandexSpellerCache(
    @Transient private val file: File = File("data/yandex_cache.json")
) : MutableMap<String, String> by loadFromJsonFile(file) {

    companion object {
        fun loadFromJsonFile(file: File): MutableMap<String, String> {
            return if (file.exists())
                Json.decodeFromStream(file.inputStream())
            else mutableMapOf()
        }
    }

    fun save() {
        Json.encodeToStream(this, file.outputStream())
    }
}

fun main() {
    val spellService = YandexSpellService()
    val extraDictionary = ExtraDictionary()

    extraDictionary.extraDictionary
        .filter { !spellService.isCorrect(it) }
        .sorted()
        .forEach { println(it) }

    spellService.saveCache()
}
