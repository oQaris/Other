import java.io.File

fun main() {
    val all = File("LetterFrequencies/rus5").bufferedReader()
        .readText().split(" ")
        .toMutableList()
    var old: List<Int> = listOf(0, 0, 0, 0, 0)
    while (true) {
        println(all.size)
        val word = all.random()
        println(word)
        val input = readln()
        if (input.startsWith("-")) {
            all.remove(word)
            println("$word removed")
            continue
        }
        var mask: List<Int>
        if (input.isBlank()) {
            mask = old
        } else {
            mask = input.map { it.toString().toInt() }
            if (mask.size != 5 || mask.any { it < 0 || it > 1 }) {
                println("drop")
                continue
            }
        }
        all.removeIf { str ->
            IntArray(5) { it }.any { idx ->
                if (mask[idx] == 0) {
                    str[idx] == word[idx]
                } else {
                    str[idx] != word[idx]
                }
            }
        }
        old = mask
    }
}
