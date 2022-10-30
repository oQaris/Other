package analmess

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class NLPKtTest {

    @Test
    fun rusWordsTest() {
        Assertions.assertEquals(
            listOf(
                "привет", "всем", "я", "живу", "в", "санкт-петербурге", "и",
                "китайский", "работа", "учёба", "кто-то", "и", "т", "д"
            ),
            ("Привет всем! Я живу в Санкт-Петербурге, I know english  и китайский." +
                    "Работа/Учёба кто-то и т.д.").rusWords()
        )
    }

    @Test
    fun nlpTest() {
        Assertions.assertTrue("Санкт-Петербург".inDictionary())
        Assertions.assertFalse("т.д".inDictionary())
    }
}