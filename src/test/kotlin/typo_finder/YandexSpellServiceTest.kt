package typo_finder

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class YandexSpellServiceTest {

    @Test
    fun toCorrect() {
        val service = YandexSpellService()

        assertEquals("", service.toCorrect(""))

        assertEquals("correct", service.toCorrect("correct"))

        val input = "приветт, как дила? у vtyz всё в парятке"
        assertEquals(
            "привет, как дела? у меня всё в порядке",
            service.toCorrect(input)
        )
    }
}
