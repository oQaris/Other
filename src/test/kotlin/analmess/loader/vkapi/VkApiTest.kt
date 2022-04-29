package analmess.loader.vkapi

import org.junit.jupiter.api.Test

internal class VkApiTest {

    val api = VkApi(
        254878066,
        "*redacted*",
        -157369801//185357991
    )

    @Test
    fun loadChatTest() {
        println(api.loadChat())
    }
}
