package telegram

import com.github.demidko.aot.WordformMeaning.lookupForMeanings

fun toWords(text: String): List<String> {
    return text.split("\\s+".toRegex())
        .map { it.replace("[^A-Za-zА-Яа-яЁё]+".toRegex(), "").lowercase() }
        .filter { it.length >= minLenWord }
        .map {
            lookupForMeanings(it).run {
                if (size > 0) get(0).lemma.toString()
                else it
            }
        }
}


fun <T> Iterable<T>.sortedCounter() = groupingBy { it }.eachCount().toList().sortedBy { (_, count) -> -count }

fun <K, V> Iterable<Pair<K, V>>.tableStr(n: Int = 20) = take(n).joinToString("\n") { (k, v) -> "$k\t\t$v" }

fun wordsFrequency(mess: List<Message>) = mess.flatMap { toWords(it.text.simpleText()) }.sortedCounter()
