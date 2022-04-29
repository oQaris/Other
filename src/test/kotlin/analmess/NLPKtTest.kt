package analmess

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class NLPTest {

    @Test
    fun tokensTest() {
        val link = "https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html"
        val data = "Привет, Джон,\t как тебе статья - $link ?"
        assertEquals(listOf("привет", "джон", "как", "тебе", "статья", link), data.tokens())
    }

    @Test
    fun removeSparePartsOfSpeechTest() {
        val data = listOf("это", "как", "у", "тебя", "дела")
        assertAll(
            { assertEquals(listOf("дела"), data.removeSparePartsOfSpeech()) },
            { assertEquals(listOf("это", "дела"), data.removeSparePartsOfSpeech(saveLonger = 3)) },
            { assertEquals(emptyList<String>(), data.removeSparePartsOfSpeech(removeShorter = 5)) },
            { assertEquals(data - "у", data.removeSparePartsOfSpeech(removeShorter = 6, saveLonger = 1)) }
        )
        assertEquals(listOf("непонятноеслово"), listOf("непонятноеслово").removeSparePartsOfSpeech())
    }

    @Test
    fun lemmasTest() {
    }
}