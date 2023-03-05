package other.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.*

fun main() {
    val path = "C:\\Users\\oQaris\\Desktop\\Сочи 2022"
    val format = SimpleDateFormat("yyyy-MM-dd HH.mm.ss")

    listFilesWithSubFolders(File(path))
        .also { println("Обрабатывается ${it.size} файлов...") }
        .forEach { file ->
            val extension = file.toPath().toString().takeLastWhile { it != '.' }.lowercase()
            val newPath = path + "\\" + format.format(Date(file.lastModified())) + "." + extension
            val newFile = File(newPath)

            newFile.createNewFile()
            Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
}

fun listFilesWithSubFolders(dir: File): Set<File> {
    val files = mutableSetOf<File>()
    for (file in dir.listFiles()!!) {
        if (file.isDirectory)
            files.addAll(listFilesWithSubFolders(file))
        else files.add(file)
    }
    return files
}
