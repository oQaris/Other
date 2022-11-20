package typo_finder

import java.io.File

class ExtraDictionary(src: File = File("extra_dictionary.txt")) {

    fun inExtraDict(word: String) = extraDictionary.any {
        word.startsWith(it) && word.length - it.length < 4//5.coerceAtMost(word.length / 3)
    }

}

val extraDictionary = setOf(
    "рефакторинг",
    "токен",
    "перевзвешива",
    "весовки",
    "префлоп",
    "флоп",
    "постфлоп",
    "фолд",
    "блайнд",
    "рейз",
    "нейросет",
    "ривер",
    "селфпле",
    "синглтон",
    "лог",
    "сериализац",
    "десериализац",
    "ситаут",
    "коллюдер",
    "шоудаун",
    "оффлайн",
    "лимпер",
    "техчат",
    "непросчитанных",
    "коллюдер",
    "монте-карло",
    "олл-ин",
    "тимпле",
    "быстрокнопк",
    "раскомментирова",
    "баттон",
    "регуляр",
    "многопотоково",
    "лимп",
    "баунд",
    "стат",
    "одномастн",
    "раздач",
    "патч",
)