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

class Table private(private val padding: Int = 1, private val columns: MutableList<List<String>> = mutableListOf()) :
    MutableList<List<String>> by columns {

    fun print() = println(formatedRows().joinToString("\n"))

fun formatedRows() = buildList{
if (columns.isEmpty() || columns.all { it.isEmpty() }) return
        val widths = columns.map { col -> col.maxOf { it.length } }
        val height = columns.maxOf { it.size }
        for (row in 0 until height) {
var rowStr = "" 
            for (col in columns.indices) {
                val content = if (row < columns[col].size) columns[col][row] else ""
                rowStr+=content.padEnd(widths[col] + padding)
            }
            add(rowStr) 
        } 
} 

    fun addColumn(header: String, data: Iterable<Any>) {
        add(listOf(header) + data.map { it.toString() })
    }

companion object {

class Builder(val padding: Int){
private val columns = mutableListOf<List<String>>() 
private abstract class Appender{
val columns: MutableList<List<String>>
fun add(vararg data: Any) 
}
private class RowsAppender{
fun add(vararg rowData: Any) {
if(columns.isEmpty())
columns.addAll(rowData.map{listOf(it.toString())})
else columns.zip(rowData).forEach{col, el -> col.add(el.toString())}
}
}
private class ColumnsAppender{
fun add(vararg columnData: Any) {
columns.add(columnData.map { it.toString() })  
} 
}
} 
fun withRows(append: RowAppender.() -> Unit):Table{
RowAppender(columns).append()
return Table(padding, columns) 
} 

fun withColumns(append: ColumnAppender.() -> Unit):Table{
ColumnAppender(columns).append()
return Table(padding, columns) 
}

private fun buildTable(appender:Appender): Table{
//ToDo
} 
}
}
} 
