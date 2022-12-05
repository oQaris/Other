package properties_collector

import java.io.File
import java.nio.file.Path
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val help = """
        args[0] - executable file (exe, bat, cmd, jar, etc.)
        args[1] - original folder 'properties'
        args[2] - true if for each new launch it is required to copy the folder 
                  where the executable file is located (default is false).
        args[3] - true if it is necessary to redirect the output of the running 
                  program to the current console. (default is true).
        """.trimIndent()
    try {
        check(args.size in 2..4) { help }
        val origProps = File(args[1])
        check(origProps.name == "properties" && origProps.exists())
        { "${origProps.absolutePath} is not valid" }

        val exeFile = Path.of(args[0])
        val isBackup = if (args.size >= 3) args[2].toBoolean() else false
        val isFullLog = if (args.size >= 4) args[3].toBoolean() else true

        PropertiesCollector(origProps.toPath(), exeFile, isBackup, isFullLog).run()

    } catch (e: Throwable) {
        check(false) { e.toString() }
    }
}

inline fun check(value: Boolean, lazyMessage: () -> String) {
    if (!value) {
        println(lazyMessage())
        exitProcess(1)
    }
}
