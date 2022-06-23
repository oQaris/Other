package other.decoder

import com.github.shiguruikai.combinatoricskt.Combinatorics
import com.github.shiguruikai.combinatoricskt.combinations

private data class FilmVar(val film: String, val genre: String, val rating: String, val award: String)

fun main() {
    val n = 4
    val films = setOf("ЖБ", "ДП", "ЭТ", "БС")
    val genres = setOf("Д", "К", "Б", "М")
    val ratings = setOf("67", "72", "77", "83")
    val awards = setOf("0", "1", "2", "3")

    Combinatorics.cartesianProduct(
        films, genres, ratings, awards
    ).map { FilmVar(it[0], it[1], it[2], it[3]) }
        // Ограничения
        .filter { variable ->
            // A -> B = (!A || B)
            // 1 - Боевик, комедия и «Электрик и ты» получили 0, 1 и 3 награды
            (!(variable.genre in listOf("Б", "К") || variable.film == "ЭТ") || variable.award in listOf("0", "1", "3"))
                    && (variable.film != "ЭТ" || variable.genre !in listOf("Б", "К"))
                    // 2 - Комедия «Жуя буррито» получила больше одной награды, а её зрительский рейтинг 7.2
                    && (variable.film != "ЖБ" || (variable.award.toInt() > 1 && variable.rating == "72"))
                    // 3 - Лента «Дождевые пингвины» была награждена дважды
                    && (variable.film != "ДП" || variable.award == "2")
                    // 4 - Кино с рейтингом 7.7 - это не мюзикл и не драма
                    && (variable.rating != "77" || variable.genre !in listOf("М", "Д"))
                    // 5 - Максимальный рейтинг - у драмы, и это не «Электрик и ты»
                    && (variable.rating != "83" || (variable.genre == "Д" && variable.film != "ЭТ"))
                    // 6 - Фильм с одной наградой - это не боевик и не «Жуя буррито»
                    && (variable.award != "1" || (variable.film != "ЖБ" && variable.genre != "Б"))
        }.toList()
        .combinations(n).filter { newVars ->
            // Все различны
            newVars.groupBy { it.film }.size == n
                    && newVars.groupBy { it.genre }.size == n
                    && newVars.groupBy { it.rating }.size == n
                    && newVars.groupBy { it.award }.size == n
        }.forEach { row ->
            println(row.joinToString("\n", postfix = "\n---------") {
                "${it.film} ${it.genre} ${it.rating} ${it.award} "
            })
        }
}
