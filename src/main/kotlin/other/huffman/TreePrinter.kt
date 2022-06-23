package other.huffman

import kotlin.math.max
import kotlin.math.pow

class TreePrinter(private val root: Node) {
    private val valueFormatter: (Node) -> String = { it.data?.format() ?: "○" }

    fun printHorizontal() = printHorizontalHelper(root)

    private fun printHorizontalHelper(curNode: Node?, prefix: String = "", isLeft: Boolean = false) {
        if (curNode != null) {
            println(prefix + (if (isLeft) "|-- " else "\\-- ") + valueFormatter.invoke(curNode))
            printHorizontalHelper(
                curNode.left,
                prefix + if (isLeft) "|   " else "    ",
                true
            )
            printHorizontalHelper(
                curNode.right,
                prefix + if (isLeft) "|   " else "    ",
                false
            )
        }
    }

    fun printVertical() {
        val maxLevel: Int = maxLevel(root)
        printNodeInternal(listOf(root), 1, maxLevel)
    }

    private fun printNodeInternal(nodes: List<Node?>, level: Int, maxLevel: Int) {
        if (nodes.isEmpty() || nodes.all { it == null }) return
        val floor = maxLevel - level
        val endgeLines = 2.0.pow(max(floor - 1, 0).toDouble()).toInt()
        val firstSpaces = 2.0.pow(floor.toDouble()).toInt() - 1
        val betweenSpaces = 2.0.pow((floor + 1).toDouble()).toInt() - 1
        printWhitespaces(firstSpaces)
        val newNodes: MutableList<Node?> = ArrayList()
        for (node in nodes) {
            if (node != null) {
                print(valueFormatter.invoke(node))
                newNodes.add(node.left)
                newNodes.add(node.right)
            } else {
                newNodes.add(null)
                newNodes.add(null)
                print(" ")
            }
            printWhitespaces(betweenSpaces)
        }
        println("")
        for (i in 1..endgeLines) {
            for (j in nodes.indices) {
                printWhitespaces(firstSpaces - i)
                if (nodes[j] == null) {
                    printWhitespaces(endgeLines + endgeLines + i + 1)
                    continue
                }
                if (nodes[j]?.left != null) print("/") else printWhitespaces(1)
                printWhitespaces(i + i - 1)
                if (nodes[j]?.right != null) print("\\") else printWhitespaces(1)
                printWhitespaces(endgeLines + endgeLines - i)
            }
            println("")
        }
        printNodeInternal(newNodes, level + 1, maxLevel)
    }

    private fun printWhitespaces(count: Int) {
        for (i in 0 until count) print(" ")
    }

    private fun maxLevel(node: Node?): Int {
        return if (node == null) 0 else max(
            maxLevel(node.left),
            maxLevel(node.right)
        ) + 1
    }
}
