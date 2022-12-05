package processors

/**
 * Возвращает список комментариев для заданного типа контента.
 */
fun String.comments(holder: CommentRegex): List<String> {
    require(holder != CommentRegex.STRING)
    return holder.regex.findAll(this)
        .map { res ->
            res.groupValues[2].takeIf {
                it.isNotEmpty()
            } ?: res.groupValues[3]
        }.filter { it.isNotEmpty() }
        .toList()
}

enum class CommentRegex(val regex: Regex) {
    /**
     * Строковая переменная в двойных или одинарных кавычках, с учётом экранирования символом \. Группа без захвата.
     * - Группа 1 - тип кавычки.
     * - Группа без захвата - содержание строки.
     */
    STRING("(\"|')(?:\\\\(?!\\1)|\\\\\\1|.)*?\\1".toRegex()),

    /**
     * Комментарии в C, C++, PHP, C#, Java и JavaScript коде.
     * - Группа 2 - однострочные комментарии.
     * - Группа 3 - javadoc и многострочные комментарии.
     */
    JAVA("${STRING.regex}|//+(.*)|(?s)/\\*+(.*?)\\*/".toRegex()),

    /**
     * Комментарии в Shell скриптах и Python коде.
     * - Группа 2 - однострочные комментарии.
     */
    PY("${STRING.regex}|#+(.*)".toRegex()),

    /**
     * Комментарии в HTML, XML, XHTML, XAML разметке.
     * - Группа 2 - многострочные комментарии.
     */
    HTML("${STRING.regex}|(?s)/<!--(.*?)-->".toRegex()),

    /**
     * Комментарии в PL/SQL, Ада, Lua.
     * - Группа 2 - однострочные комментарии.
     */
    SQL("${STRING.regex}|--(.*)".toRegex()),

    /**
     * Конфигурационные (ini) файлы, файлы реестра Windows (REG), ассемблер.
     * - Группа 2 - однострочные комментарии.
     */
    CONF("${STRING.regex}|;(.*)".toRegex()),
}
