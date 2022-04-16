package telegram

import com.github.demidko.aot.PartOfSpeech.*
import com.github.demidko.aot.WordformMeaning.lookupForMeanings

val setUrl = mutableSetOf<String>()

/**
 * Разделить по белому пространству и знакам препинания (кроме ссылок), перевести в нижний регистр
 */
fun String.tokens(): List<String> {
    return split("[\\p{Z}]".toRegex()).flatMap { part ->
        if (part.isURL())
            listOf(part).apply { setUrl.add(part) }
        else part.split("[\\p{P}]".toRegex())
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
 * Если [removeShorter] не null, то удалить слова короче [removeShorter]
 * Если [saveLonger] не null, то оставить слова длинее [saveLonger]
 */
fun Iterable<String>.removeSparePartsOfSpeech(removeShorter: Int? = null, saveLonger: Int? = 3): List<String> {
    val auxiliaryPartsOfSpeech = listOf(Particle, Pretext, Union, Pronoun /*PronounAdjective, Interjection*/)
    return filterNot {
        if (it.length < (removeShorter ?: 0)) return@filterNot true
        if (it.length > (saveLonger ?: Int.MAX_VALUE)) return@filterNot false
        // если true, то удаляем
        lookupForMeanings(it).any { mean ->
            mean.partOfSpeech in auxiliaryPartsOfSpeech
        }
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
