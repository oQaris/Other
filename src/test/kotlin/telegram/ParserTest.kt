package telegram

import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

internal class ParserTest {

    @Test
    fun parseMessagesTest() {
        val parser = Parser(File("data/chat.json"))
        val actual = parser.parseChat()

        val expected = Chat(
            178051894, "Дима Патока", "personal_chat", listOf(
                Message(
                    6364, "message", LocalDateTime.parse("2021-09-29T12:25:55"),
                    "o'Qaris", "user876213253", null,
                    Text(listOf(TextItem("ну так это для \"Расписания\"? 👀")))
                ),
                Message(
                    6366, "message", LocalDateTime.parse("2021-09-30T01:00:34"),
                    "Дима Патока", "user178051894", null,
                    Text(listOf(TextItem("У рыболова же на "), TextItem("mail.ru", "link"), TextItem(" почта?")))
                ),
                Message(
                    6367, "message", LocalDateTime.parse("2021-09-30T10:27:24"),
                    "o'Qaris", "user876213253", null,
                    Text(listOf(TextItem("Ага, "), TextItem("alexander_rybalov@mail.ru", "email"), TextItem("")))
                ),
                Message(
                    18894, "message", LocalDateTime.parse("2021-11-10T02:43:47"),
                    "Милка ❤", "user983918040", null,
                    Text(listOf(TextItem(""))), "(File not included. Change data exporting settings to download.)",
                    null, "voice_message", null, "audio/ogg", 148
                ),
                Message(
                    44627, "service", LocalDateTime.parse("2021-11-30T11:53:11"),
                    "System", "System", null,
                    Text(listOf(TextItem("")))
                ),
                Message(
                    109958, "message", LocalDateTime.parse("2022-03-28T13:32:45"),
                    "Дима Патока", "user178051894", null,
                    Text(listOf(TextItem("ну например"))),
                    "(File not included. Change data exporting settings to download.)", null, null, null, null
                ),
                Message(
                    110010, "message", LocalDateTime.parse("2022-03-28T15:48:09"),
                    "Дима Патока", "user178051894", null,
                    Text(listOf(TextItem("красиво, конечно"))),
                    null, "(File not included. Change data exporting settings to download.)", null, null, null
                ),
                Message(
                    110034, "message", LocalDateTime.parse("2022-03-28T16:02:26"),
                    "o'Qaris", "user876213253", 110032,
                    Text(listOf(TextItem("Во ++")))
                ),
            )
        )
        assertEquals(expected, actual)
    }
}
