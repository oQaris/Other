import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

fun main() {
    val sourceFolder = File("C:\\Users\\Oqaris\\Desktop\\Tour")
    val destinationFolder = File("X:\\oqaris")
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

    val files = sourceFolder.listFiles()?.toList()

    files?.let {
        val sortedFiles = it.sortedBy { file -> file.lastModified() }

        sortedFiles.forEach { file ->
            val destinationFile = File(destinationFolder, file.name)
            println("${file.name} (${dateFormat.format(Date.from(Instant.ofEpochMilli(file.lastModified())))})")
            Files.move(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            TimeUnit.SECONDS.sleep(1)
        }
    }
}
