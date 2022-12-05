package analmess

class Table private constructor(
    private val padding: Int = 1,
    private val columns: List<List<String>> = mutableListOf()
) : List<List<String>> by columns {

    fun print() = println(formattedRows().joinToString("\n"))

    fun formattedRows() = buildList {
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

        fun with(
            padding: Int,
            appender: (MutableList<MutableList<String>>) -> Appender,
            append: Appender.() -> Unit
        ): Table {
            val columns = mutableListOf<MutableList<String>>()
            return buildTable(padding, columns, appender.invoke(columns), append)
        }

        private fun buildTable(
            padding: Int,
            columns: List<List<String>>,
            appender: Appender,
            append: Appender.() -> Unit
        ): Table {
            appender.append()
            return Table(padding, columns)
        }
    }
}
