package telegram

import com.github.demidko.aot.PartOfSpeech.*
import com.github.demidko.aot.WordformMeaning.lookupForMeanings

val setUrl = mutableSetOf<String>()

/**
 * Разделить по белому пространству и знакам препинания (кроме ссылок), перевести в нижний регистр
 */
fun String.tokens(): List<String> {
    return split("[\\p{Z}]".toRegex()).flatMap {
        if (!it.isURL())
            it.split("[\\p{P}]".toRegex())
        else listOf(it).apply { setUrl.add(it) }
    }.filter { it.isNotEmpty() }
}

/**
 * Оставить только слова из русских и английских букв, цифр и перевести в нижний регистр
 */
fun Iterable<String>.words(): List<String> {
    return flatMap { part ->
        val buf =
            part.split("[^0-9A-Za-zА-Яа-яЁё]+".toRegex())
                .map { it.lowercase() }
        buf
    }.filter { it.isNotEmpty() }
}

/**
 * Удалить служебные части речи. Если [minLenWord] не null, то удалить слова короче  [minLenWord]
 */
fun Iterable<String>.removeAuxiliaryPartsOfSpeech(minLenWord: Int? = null): List<String> {
    //TODO Добавить soft удаление
    return filter {
        if (it.length < (minLenWord ?: 0)) return@filter false

        val mean = lookupForMeanings(it)
        if (mean.isNotEmpty())
            when (mean[0].partOfSpeech) {
                Particle, Pretext, Union, Pronoun, PronounAdjective, Interjection, Adverb -> false
                else -> true
            } else true
    }
}

/**
 * Привести слов из словаря к нормальной форме (первого лица, единственного числа)
 */
fun Iterable<String>.lemmas() = map {
    lookupForMeanings(it).run {
        if (size > 0) get(0).lemma.toString()
        else it // если нет в словаре
    }
}

fun Iterable<String>.emoji(): List<String> {
    val emojiRegexp = "[\\x{0001f300}-\\x{0001f64f}]|[\\x{0001f680}-\\x{0001f6ff}]".toRegex()
    // val r2 = "[\ud83c\udf00-\ud83d\ude4f]|[\ud83d\ude80-\ud83d\udeff]".toRegex()
    return flatMap { part ->
        emojiRegexp.findAll(part).map { it.value }
    }.filter { it.isNotEmpty() }
}
