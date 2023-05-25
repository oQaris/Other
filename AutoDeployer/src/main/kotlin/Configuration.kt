import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap
import kotlinx.serialization.properties.encodeToMap

@Serializable
data class Configuration(
    private val origProps: String,
    private val exeFile: String,
    private val isBackup: Boolean = false,
    private val isFullLog: Boolean = true
)

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    val data = Configuration("kotlinx.serialization", "kotlin")
    val map = Properties.encodeToMap(data)
    map.forEach { (k, v) -> println("$k = $v") }

    val propsMap = System.getProperties().run {
        stringPropertyNames().associateWith { getProperty(it) }
    }
    val configuration = Properties.decodeFromMap<Configuration>(propsMap)
}
