package decoder

import com.github.shiguruikai.combinatoricskt.Combinatorics
import com.github.shiguruikai.combinatoricskt.combinations
import kotlin.math.abs

private enum class House {
    One, Two, Three, Four, Five
}

private enum class Color {
    Yellow, Blue, Red, White, Green
}

private enum class Nationality {
    Norwegian, Ukrainian, Englishman, Spaniard, Japanese
}

private enum class Drink {
    Water, Tea, Milk, Juice, Coffee
}

private enum class Cigarettes {
    Kool, Chesterfield, OldGold, LuckyStrike, Parliament
}

private enum class Animal {
    Fox, Horse, Snails, Dog, Zebra
}

private data class Variable(
    val house: House,
    val color: Color,
    val nationality: Nationality,
    val drink: Drink,
    val cigarettes: Cigarettes,
    val animal: Animal,
)

private infix fun Boolean.impl(that: Boolean) = !this || that

fun main() {
    val n = 5

    val vars = Combinatorics.cartesianProduct(
        House.values().map { it.toString() },
        Color.values().map { it.toString() },
        Nationality.values().map { it.toString() },
        Drink.values().map { it.toString() },
        Cigarettes.values().map { it.toString() },
        Animal.values().map { it.toString() }
    ).map {
        Variable(
            House.valueOf(it[0]),
            Color.valueOf(it[1]),
            Nationality.valueOf(it[2]),
            Drink.valueOf(it[3]),
            Cigarettes.valueOf(it[4]),
            Animal.valueOf(it[5])
        )
    }.filter { variable ->
        // Ограничения
        (variable.nationality == Nationality.Englishman).impl(variable.color == Color.Red)
                && (variable.nationality == Nationality.Spaniard).impl(variable.animal == Animal.Dog)
                && (variable.color == Color.Green).impl(variable.drink == Drink.Coffee)
                && (variable.nationality == Nationality.Ukrainian).impl(variable.drink == Drink.Tea)
                && (variable.cigarettes == Cigarettes.OldGold).impl(variable.animal == Animal.Snails)
                && (variable.color == Color.Yellow).impl(variable.cigarettes == Cigarettes.Kool)
                && (variable.house == House.Three).impl(variable.drink == Drink.Milk)
                && (variable.nationality == Nationality.Norwegian).impl(variable.house == House.One)
                && (variable.cigarettes == Cigarettes.LuckyStrike).impl(variable.drink == Drink.Juice)
                && (variable.nationality == Nationality.Japanese).impl(variable.cigarettes == Cigarettes.Parliament)
    }.toList()

    vars.combinations(n).filter { newVars ->
        // Все различны
        newVars.groupBy { it.house }.size == n
                && newVars.groupBy { it.color }.size == n
                && newVars.groupBy { it.nationality }.size == n
                && newVars.groupBy { it.drink }.size == n
                && newVars.groupBy { it.cigarettes }.size == n
                && newVars.groupBy { it.animal }.size == n
    }.filter { newVars ->
        // Комбинированные условия
        run {
            val gre = newVars.first { it.color == Color.Green }
            val whi = newVars.first { it.color == Color.White }
            gre.house.ordinal - 1 == whi.house.ordinal
        } && run {
            val cig = newVars.first { it.cigarettes == Cigarettes.Chesterfield }
            val fox = newVars.first { it.animal == Animal.Fox }
            abs(cig.house.ordinal - fox.house.ordinal) == 1
        } && run {
            val cig = newVars.first { it.cigarettes == Cigarettes.Kool }
            val hor = newVars.first { it.animal == Animal.Horse }
            abs(cig.house.ordinal - hor.house.ordinal) == 1
        } && run {
            val nor = newVars.first { it.nationality == Nationality.Norwegian }
            val blu = newVars.first { it.color == Color.Blue }
            abs(nor.house.ordinal - blu.house.ordinal) == 1
        }
    }.forEach { row ->
        println(row.joinToString("\n", postfix = "\n---------") {
            "${it.house} ${it.color} ${it.nationality} ${it.drink} ${it.cigarettes} ${it.animal}"
        })
    }
}
