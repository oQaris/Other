package properties_collector

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PropUtilsKtTest {

    @Test
    fun extractPropertyTest() {
        assertEquals(
            "/nets/file-net.txt",
            extractProperty("FileNotFoundException: properties/nets/file-net.txt")
        )
    }
}
