package huffman

import java.util.*

fun main() {
    val input = "beep boop beer!"
    val huffman = input.groupingBy { it.uppercaseChar() }.eachCount()

    val hafPriorityQueue = PriorityQueue(Comparator
        .comparing<Node?, Int?> { it.value }
        .thenComparing { node -> node.data?.let { input.indexOf(it) } ?: 0 })

    huffman.forEach { (ch, count) -> hafPriorityQueue.add(Node(data = ch, value = count)) }

    while (hafPriorityQueue.size > 1) {
        val node1 = hafPriorityQueue.poll()
        val node2 = hafPriorityQueue.poll()
        hafPriorityQueue.add(Node(left = node1, right = node2, value = node1.value + node2.value))
    }

    val startNode = hafPriorityQueue.single()
    TreePrinter(startNode).printVertical()
    println()

    println("Символ\tЧисло появлений\tДлина кодового слова\tКодовое слово")
    val table = huffmanTable(startNode)
    println(table.joinToString("\n"))
    println()

    println("L = " + table.sumOf { it.codeLen * it.numApp })
    println()
    println("Закодированное слово:")
    println(input.map { ch ->
        table.first { it.symbol == ch.uppercaseChar() }.codeword
    }.joinToString(" "))
}

data class Node(
    val left: Node? = null,
    val right: Node? = null,
    val data: Char? = null,
    val value: Int
)

data class Row(val symbol: Char, val numApp: Int, val codeLen: Int, val codeword: String) {

    override fun toString() = symbol.format() + "\t\t\t\t" + numApp + "\t\t\t\t" + codeLen + "\t\t\t\t" + codeword
}

fun Char.format() = when (this) {
    ' ' -> "Пробел"
    '\n' -> "Абзац"
    '\t' -> "Табуляция"
    else -> toString()
}

fun huffmanTable(startNode: Node): List<Row> {
    val out = mutableListOf<Row>()
    tableHelper(out, startNode)
    out.sortByDescending { it.numApp }
    return out
}

fun tableHelper(rows: MutableList<Row>, curNode: Node, bytes: List<Int> = listOf()) {
    fun checkAndRecursive(child: Node?, byte: Int) {
        if (child != null)
            tableHelper(rows, child, bytes + byte)
    }
    if (curNode.data != null)
        rows.add(Row(curNode.data, curNode.value, bytes.size, bytes.joinToString("")))
    checkAndRecursive(curNode.left, 0)
    checkAndRecursive(curNode.right, 1)
}
