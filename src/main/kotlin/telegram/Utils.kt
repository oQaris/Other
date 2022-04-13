package telegram

val setRemWords = mutableSetOf<String>()

fun <T> Iterable<T>.sortedCounter() = groupingBy { it }.eachCount().toList().sortedBy { (_, count) -> -count }

fun <K, V> Iterable<Pair<K, V>>.tableStr(n: Int = 20) = take(n).joinToString("\n") { (k, v) -> "$k\t\t$v" }

fun wordsFrequency(mess: List<Message>) = mess.flatMap {
    val start = it.text.simpleText().tokens().words()
    val end = start.removeAuxiliaryPartsOfSpeech()
    setRemWords.addAll(start - end.toSet())
    end.lemmas()
}.sortedCounter()

fun emojiFrequency(mess: List<Message>) = mess.flatMap {
    it.text.simpleText().tokens().emoji()
}.sortedCounter()
