package telegram

import com.github.demidko.aot.PartOfSpeech
import com.github.demidko.aot.PartOfSpeech.*
import com.github.demidko.aot.WordformMeaning.lookupForMeanings

/**
 * Разделить по белому пространству и знакам препинания (кроме ссылок), перевести в нижний регистр
 */
fun String.tokens(): List<String> {
    return split("\\p{Space}".toRegex()).flatMap { part ->
        if (part.isURL()) listOf(part)
        else part.split("\\p{P}".toRegex())
            .map { it.lowercase() }
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
 * Удалить служебные части речи.
 * Вне зависимости от части речи:
 * Если [removeShorter] не null, то удалить слова, все леммы которого короче [removeShorter].
 * Если [saveLonger] не null, то оставить слова, хотя бы одна леммы которого длиннее [saveLonger]
 */
fun Iterable<String>.removeSparePartsOfSpeech(removeShorter: Int? = null, saveLonger: Int? = 5): List<String> {
    val auxiliaryPartsOfSpeech =
        setOf<PartOfSpeech>(Pretext, Particle, Union, Pronoun, PronounAdjective/* Interjection, Predicative*/)
    return filterNot { word ->
        val means = lookupForMeanings(word)
        val lemmas = //todo заменить на .lemmas() ?
            if (means.isEmpty()) listOf(word)
            else means.map { it.lemma.toString() }
        // если true, то удаляем
        if (lemmas.any { it.length > (saveLonger ?: Int.MAX_VALUE) })
            return@filterNot false
        if (lemmas.all { it.length < (removeShorter ?: 0) })
            return@filterNot true
        means.any { it.partOfSpeech in auxiliaryPartsOfSpeech }
    }
}

/**
 * Привести слов из словаря к нормальной форме (первого лица, единственного числа)
 */
fun Iterable<String>.lemmas() = map { word ->
    word.lemmas().minByOrNull { it.length }!!
}

/**
 * Возвращает список нормальных форм слова
 */
fun String.lemmas(): List<String> {
    val means = lookupForMeanings(this)
    return if (means.isEmpty()) listOf(this)
    else means.map { it.lemma.toString() }
}
