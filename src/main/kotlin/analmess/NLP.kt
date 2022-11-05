package analmess

import com.github.demidko.aot.PartOfSpeech
import com.github.demidko.aot.PartOfSpeech.*
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import org.vosk.Model
import java.io.DataInputStream
import java.io.File
import javax.sound.sampled.AudioSystem


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
 * Оставить только слова из русских и английских букв, цифр и перевести в нижний регистр.
 */
fun Iterable<String>.words(): List<String> {
    val needReg = "[^0-9A-Za-zА-Яа-яЁё]+".toRegex()
    return flatMap { part ->
        part.split(needReg)
            .map { it.lowercase() }
    }.filter { it.isNotEmpty() }
}

/**
 * Оставить только слова из русских букв (включая слова через дефис), перевести в нижний регистр.
 */
fun String.rusWords(): List<String> {
    val needReg = "[А-яЁё]+(-[А-яЁё]+)?".toRegex()
    return needReg.findAll(this)
        .map { it.value }
        .filter { it.isNotEmpty() }
        .map { it.lowercase() }
        .toList()
}

/**
 * Возвращает список комментариев для заданного типа контента.
 */
fun String.comments(holder: CommentRegex): List<String> {
    require(holder != CommentRegex.STRING)
    return holder.regex.findAll(this)
        .map { res ->
            res.groupValues[2].takeIf {
                it.isNotEmpty()
            } ?: res.groupValues[3]
        }.filter { it.isNotEmpty() }
        .toList()
}

enum class CommentRegex(val regex: Regex) {
    /**
     * Строковая переменная в двойных или одинарных кавычках, с учётом экранирования символом \. Группа без захвата.
     * - Группа 1 - тип кавычки.
     * - Группа без захвата - содержание строки.
     */
    STRING("(\"|')(?:\\\\(?!\\1)|\\\\\\1|.)*?\\1".toRegex()),

    /**
     * Комментарии в C, C++, PHP, C#, Java и JavaScript коде.
     * - Группа 2 - однострочные комментарии.
     * - Группа 3 - javadoc и многострочные комментарии.
     */
    JAVA("${STRING.regex}|//+(.*)|(?s)/\\*+(.*?)\\*/".toRegex()),

    /**
     * Комментарии в Shell скриптах и Python коде.
     * - Группа 2 - однострочные комментарии.
     */
    PY("${STRING.regex}|#+(.*)".toRegex()),

    /**
     * Комментарии в HTML, XML, XHTML, XAML разметке.
     * - Группа 2 - многострочные комментарии.
     */
    HTML("${STRING.regex}|(?s)/<!--(.*?)-->".toRegex()),

    /**
     * Комментарии в PL/SQL, Ада, Lua.
     * - Группа 2 - однострочные комментарии.
     */
    SQL("${STRING.regex}|--(.*)".toRegex()),

    /**
     * Конфигурационные (ini) файлы, файлы реестра Windows (REG), ассемблер.
     * - Группа 2 - однострочные комментарии.
     */
    CONF("${STRING.regex}|;(.*)".toRegex()),
}

/**
 * Удалить служебные части речи.
 * Вне зависимости от части речи:
 * Если [removeShorter] не null, то удалить слова, все леммы которого короче [removeShorter].
 * Если [saveLonger] не null, то оставить слова, хотя бы одна лемма которого длиннее [saveLonger]
 */
fun Iterable<String>.removeSparePartsOfSpeech(removeShorter: Int? = null, saveLonger: Int? = 5): List<String> {
    val excludedPartsOfSpeech =
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
        means.any { it.partOfSpeech in excludedPartsOfSpeech }
    }
}

/**
 * Возвращает список нормальных форм слова (первого лица, единственного числа)
 */
fun String.lemmas(): List<String> {
    val means = lookupForMeanings(this)
    return if (means.isEmpty()) listOf(this)
    else means.map { it.lemma.toString() }
}

/**
 * Возвращает одну нормальную форму слова (короткую из часто встречающихся)
 */
fun String.lemma() = this.lemmas()
    .maxsByCount()
    .minByOrNull { it.length }!!

/**
 * true - если строка содержится в словаре русского языка
 */
fun String.inDictionary() = lookupForMeanings(this).isNotEmpty()

/**
 * Все части речи слова
 */
fun String.partsOfSpeech() = lookupForMeanings(this)
    .map { it.partOfSpeech.description }.maxsByCount()

fun printInfoFromWord(word: String) {
    println(word)
    var flag = false
    lookupForMeanings(word).forEach { mean ->
        println("\t${mean.lemma} ${mean.morphology} ${mean.partOfSpeech} ${mean.transformations}")
        flag = true
    }
    if (!flag) println("\tNone")
}


fun voiceToText() {
    val model = Model("C:\\Users\\oQaris\\Downloads\\vosk\\vosk-model-ru-0.22-compile")
    val audioFile =
        File("C:\\Users\\oQaris\\Desktop\\Telegram Desktop\\Polina\\voice_messages\\audio_487@18-10-2022_21-06-45.ogg")

    val samples: ByteArray
    val inputStream = AudioSystem.getAudioInputStream(audioFile)
    DataInputStream(inputStream).use { dis ->
        val format = inputStream.format
        samples = ByteArray((inputStream.frameLength * format.frameSize).toInt())
        dis.readFully(samples)
    }
    val rec = org.vosk.Recognizer(model, 16000f)
    rec.acceptWaveForm(samples, samples.size)
    println(rec.finalResult)
}