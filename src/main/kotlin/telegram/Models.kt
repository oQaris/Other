package telegram

import kotlinx.datetime.LocalDateTime

data class Chat(
    val id: Long,
    val name: String,
    val type: String,
    val messages: List<Message>
)

data class Message(
    val id: Long,
    val type: String,
    val date: LocalDateTime,
    val from: String,
    val from_id: String,
    val reply_to_message_id: Long? = null,
    val text: Text,

    val file: String? = null,
    val photo: String? = null,
    val media_type: String? = null,
    val sticker_emoji: String? = null,
    val mime_type: String? = null,
    val duration_seconds: Int? = null
)

data class Text(val parts: List<TextItem>) {

    fun simpleText() = parts.joinToString("") { it.text }
}

data class TextItem(val text: String, val type: String? = null)

// Для вывода

class Table private constructor(
    private val padding: Int = 1,
    private val columns: List<List<String>> = mutableListOf()
) :
    List<List<String>> by columns {

    fun print() = println(formattedRows().joinToString("\n"))

    private fun formattedRows() = buildList {
        if (columns.isEmpty() || columns.all { it.isEmpty() }) return@buildList
        val widths = columns.map { col -> col.maxOf { it.length } }
        val height = columns.maxOf { it.size }
        for (row in 0 until height) {
            var rowStr = ""
            for (col in columns.indices) {
                val content = if (row < columns[col].size) columns[col][row] else ""
                rowStr += content.padEnd(widths[col] + padding)
            }
            add(rowStr)
        }
    }

    companion object Builder {
        private val columns = mutableListOf<MutableList<String>>()

        abstract class Appender(val columns: MutableList<MutableList<String>>) {
            abstract fun add(data: Iterable<Any>)
            fun add(header: String, data: Iterable<Any>) {
                add(listOf(header) + data)
            }
        }

        class RowsAppender(columns: MutableList<MutableList<String>>) : Appender(columns) {
            override fun add(data: Iterable<Any>) {
                if (columns.isEmpty())
                    columns.addAll(data.map { mutableListOf(it.toString()) })
                else columns.zip(data).forEach { (col, el) -> col.add(el.toString()) }
            }
        }

        class ColumnsAppender(columns: MutableList<MutableList<String>>) : Appender(columns) {
            override fun add(data: Iterable<Any>) {
                columns.add(data.map { it.toString() }.toMutableList())
            }
        }

        fun withRows(padding: Int, append: Appender.() -> Unit) =
            buildTable(padding, RowsAppender(columns), append)

        fun withColumns(padding: Int, append: Appender.() -> Unit) =
            buildTable(padding, ColumnsAppender(columns), append)

        private fun buildTable(padding: Int, appender: Appender, append: Appender.() -> Unit): Table {
            columns.clear() //todo костыль
            appender.append()
            return Table(padding, columns)
        }
    }
}
