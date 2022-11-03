package analmess

import org.junit.jupiter.api.Assertions.*
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
        val unknown = listOf("непонятноеслово", "")
        assertEquals(unknown, unknown.removeSparePartsOfSpeech())
    }

    @Test
    fun lemmasTest() {
        assertEquals("после", "после".lemma())
    }

    @Test
    fun rusWordsTest() {
        assertEquals(
            listOf(
                "привет", "всем", "я", "живу", "в", "санкт-петербурге", "и",
                "китайский", "работа", "учёба", "кто-то", "и", "т", "д"
            ),
            ("Привет всем! Я живу в Санкт-Петербурге, I know english  и китайский." +
                    "Работа/Учёба кто-то и т.д.").rusWords()
        )
    }

    @Test
    fun commentsTest() {
        println(("s = \"asd\"fds\"\n" +
                "\"//\\\\////\\\\\\\\\\/**/\"\n" +
                "''\"\"l;l;;l;'\n" +
                "logger.error(\"\", ex);\n" +
                "        }\n" +
                "\"\"\n" +
                "    }\n" +
                "test //fdds\n" +
                "////kjoinoi\n" +
                "     lklkl //kjpo\n" +
                "/*dsf \n" +
                "*/\n" +
                "/** dsf \n" +
                "*/\n" +
                "/**/").javaComments())
    }

    @Test
    fun nlpTest() {
        assertTrue("Санкт-Петербург".inDictionary())
        assertFalse("т.д".inDictionary())
    }
}
