package properties_collector

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PropUtilsKtTest {

    @Test
    fun extractPropertyTest() {
        assertEquals(
            "nets\\file-net.txt",
            extractProperty("FileNotFoundException: properties/nets/file-net.txt")
        )
        assertEquals(
            "caches\\sp_cache\\sphalflife.v3.2.NL.min.sformat5.cache.zip",
            extractProperty(
                "Exception in thread \"main\" java.lang.IllegalStateException: " +
                        "SProfileCache.loadHalfLifesCacheLong(): load exception, cache file: " +
                        "caches/sp_cache/sphalflife.v3.2.NL.min.sformat5.cache.zip, message: " +
                        "properties\\caches\\sp_cache\\sphalflife.v3.2.NL.min.sformat5.cache.zip (...)"
            )
        )
    }

    @Test
    fun gggTest() {
        assertEquals(
            "bots:nlp:nlp2_base",
            includeModule("modules/bots/nlp/nlp2_base")
        )

        assertEquals(
            "rl_bots:ohpf:ohpfgto1_private",
            includeModule("submodules/rl_bots/ohpf/ohpfgto1_private")
        )

        includeModule("platform_module")
    }

    fun includeModule(projectDir: String): String {
        val path = listOf("modules/", "submodules/")
            .map { projectDir.substringAfter(it) }
            .firstOrNull { it != projectDir }
        requireNotNull(path) { "Modules should only be located in the 'modules/' or 'submodules/' folder" }
        val projectPath = path.replace('/', ':')
        return projectPath
    }
}
