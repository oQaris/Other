package telegram

import kotlinx.datetime.LocalDateTime

data class Message(
    val id: Long,
    val type: String,
    val date: LocalDateTime,
    val from: String,
    val from_id: String,
    val text: Text,
    val media_type: String? = null,
    val sticker_emoji: String? = null,
    val file: String? = null,
    val photo: String? = null,
)

data class Text(val parts: List<TextItem>) {

    fun simpleText() = parts.joinToString("") { it.text }
}

data class TextItem(val text: String, val type: String)

class Table(private val columns: MutableList<List<String>> = mutableListOf(), private val padding: Int = 1) :
    MutableList<List<String>> by columns {

    fun print() {
        if (columns.isEmpty() || columns.all { it.isEmpty() }) return
        val widths = columns.map { col -> col.maxOf { it.length } }
        val height = columns.maxOf { it.size }
        for (row in 0 until height) {
            for (col in columns.indices) {
                val content = if (row < columns[col].size) columns[col][row] else ""
                print(content.padEnd(widths[col] + padding))
            }
            println()
        }
    }

    fun addColumn(header: String, data: Iterable<Any>) {
        add(listOf(header) + data.map { it.toString() })
    }
}