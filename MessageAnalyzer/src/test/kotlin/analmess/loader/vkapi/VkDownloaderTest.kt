package analmess.loader.vkapi

import analmess.loader.VkDownloader
import org.junit.jupiter.api.Test

internal class VkDownloaderTest {

    private val api = VkDownloader(
        254878066,
        "vk1.a.1JtSHgIFvuJxKNyIgIxZkmoWHMsUOFrvKAtG2X7JAnyoziDNFT-PIExRGXix9Gu6-32kNKSW7vHRLCaxyKetZ7pjL7xHsHsW3Vc7gtfuC0WVPSgdDWktaNNWoHlUzjdOKEEquUCjpTItgcaa0j7i-74_Yf4fWERF6adMosHWJ96d_5flw4f09Z_fE_Lv6SGUTBOv5WC6r-oLjJuYNSTd9g",
        587172110
    )

    @Test
    fun loadChatTest() {
        println(api.loadChat().messages.take(200)
            .joinToString("\n") { it.text.simpleText() })
    }
}
