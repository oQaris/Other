package analmess

import com.vdurmont.emoji.EmojiParser
import lemma
import removeSparePartsOfSpeech
import tokens

fun wordsFrequency(mess: List<Message>): List<WordsFrequency> {
    val wordToLemma = mess.flatMap { msg ->
        val start = msg.text.simpleText().tokens()
        val end = start.removeSparePartsOfSpeech()
        //log
        setRemWords.addAll((start - end.toSet()).map { it.lemma() })
        end.zip(end.map { it.lemma() })
    }
    return wordToLemma.map { it.second }.sortedCounter().map { (lem, cnt) ->
        val srcWords = wordToLemma
            .filter { it.second == lem }
            .map { it.first }.toSet()
        val maxSrcWords = srcWords.maxsByCount()

        if (srcWords.size == 1)
            WordsFrequency(srcWords.first(), cnt, "")
        else WordsFrequency(lem, cnt, maxSrcWords.firstOrNull { it != lem } ?: "")
    }
}

data class WordsFrequency(val lemma: String, val count: Int, val original: String) {
    fun formatOrig() = if (original.isEmpty()) "" else "~$original"
}

fun emojiFrequency(mess: List<Message>) = mess.flatMap {
    EmojiParser.extractEmojis(it.text.simpleText())
}.sortedCounter()
