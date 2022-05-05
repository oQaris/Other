package analmess.loader.vkapi

import analmess.loader.VkDownloader
import org.junit.jupiter.api.Test

internal class VkDownloaderTest {

    val api = VkDownloader(
        254878066,
        "1fb6cda9ba3f0c45cbf9fcb7d766b1dbd94cb593d148127e89da54ed31095996f2e3ec4c6f1627194d521",
        587172110
    )

    @Test
    fun loadChatTest() {
        println(api.loadChat().messages.take(200)
            .joinToString("\n") { it.text.simpleText() })
    }
}
