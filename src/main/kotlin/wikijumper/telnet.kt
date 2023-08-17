package wikijumper

import java.io.File
import java.util.*

fun main() {
    val tel = ProcessBuilder("\"C:\\Program Files (x86)\\Nmap\\ncat.exe\"", "158.176.4.7", "9443").start()
    tel.inputStream.bufferedReader().lines().limit(23).forEach { println(it) }
    tel.outputStream.write("H\n".toByteArray())
    tel.outputStream.flush()
    println(tel.inputStream.bufferedReader().readLine())
    println(tel.inputStream.bufferedReader().readLine())
    var dig = 0.0
    val file = File("ctf_data_neg.csv").bufferedWriter()
    while (dig >= -180) {
        val req = Array(50) { idx ->
            String.format(Locale.ENGLISH, "%.3f", dig - idx.toDouble() / 1000)
        }.joinToString(",")
        println(req)
        Thread.sleep(100)
        tel.outputStream.write((req + '\n').toByteArray())
        tel.outputStream.flush()
        val hash = tel.inputStream.bufferedReader().readLine()
        println(hash)
        req.split(",").zip(hash.split(": ")[1].chunked(4)).forEach { (k, v) ->
            file.write("$k;$v\n")
        }
        file.flush()
        dig -= 0.05
    }
}