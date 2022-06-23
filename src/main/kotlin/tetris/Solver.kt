package tetris

import com.github.shiguruikai.combinatoricskt.combinations
import org.sat4j.core.VecInt
import org.sat4j.minisat.SolverFactory
import org.sat4j.specs.ISolver

typealias Brick = List<List<Int>>

private val bricks = listOf(
    listOf(listOf(1, 1, 1, 1)),
    listOf(
        listOf(1, 0, 0),
        listOf(1, 1, 1)
    ),
    listOf(
        listOf(0, 0, 1),
        listOf(1, 1, 1)
    ),
    listOf(
        listOf(1, 1),
        listOf(1, 1)
    ),
    listOf(
        listOf(0, 1, 1),
        listOf(1, 1, 0)
    ),
    listOf(
        listOf(0, 1, 0),
        listOf(1, 1, 1)
    ),
    listOf(
        listOf(1, 1, 0),
        listOf(0, 1, 1)
    ),
)

private val input = listOf(
    listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    listOf(0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
    listOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0),
    listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0),
    listOf(0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0),
    listOf(0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0),
    listOf(0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1),
    listOf(1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1),
)

internal val width = input.first().size
internal val height = input.size

private val values = generateSatValues(bricks)
private fun Brick.valIdx() = values.indexOf(this) + 1

fun main() {
    require(input.all { it.size == input.first().size })

    val solver = SolverFactory.newDefault()
    solver.newVar(values.size)

    for (i in input.indices)
        for (j in input.first().indices) {
            val clause = values.filter { it[i][j] == 1 }
            // покрываем все занятые клетки поля
            if (input[i][j] == 1) {
                solver.addClause(
                    clause.map { it.valIdx() }.toVec()
                )
            }// убираем все не занятые
            else {
                clause.map { -it.valIdx() }
                    .windowed(1).forEach {
                        solver.addClause(it.toVec())
                    }
            }
        }
    // запрещаем пересечения
    values.combinations(2).forEach { sl2 ->
        val (br1, br2) = sl2[0] to sl2[1]
        val isCollapse = br1.zip(br2).any { (b1row, b2row) ->
            b1row.zip(b2row).any { it.first == 1 && it.second == 1 }
        }
        if (isCollapse)
            solver.addClause(
                listOf(
                    -br1.valIdx(),
                    -br2.valIdx()
                ).toVec()
            )
    }
    // Используем фигуру только 1 раз
    values.groupBy { it.orig }.forEach { (_, slices) ->
        slices.combinations(2).forEach { sl2 ->
            val (br1, br2) = sl2[0] to sl2[1]
            solver.addClause(
                listOf(
                    -br1.valIdx(),
                    -br2.valIdx()
                ).toVec()
            )
        }
    }

    var cnt = 0
    solver.isVerbose = true
    while (true) {
        if (solver.isSatisfiable) {
            println("Satisfiable!")
            println("Переменных: " + solver.nVars())
            println("Ограничений: " + solver.nConstraints())
            println()
            prettyPrint(solver.primeImplicant())
            println()
            cnt++
            // для повторного поиска
            addInverse(solver)
        } else {
            if (cnt == 0) println("Not satisfiable")
            else println("Number of solutions: $cnt")
            return
        }
    }
}

fun Collection<Int>.toVec() = VecInt(this.toIntArray())

private fun prettyPrint(primeImplicant: IntArray) {
    var form = zeroBrick()
    primeImplicant.forEach { idVal ->
        if (idVal > 0) {
            val value = values[idVal - 1]
            form = plus(form, prod(value, bricks.indexOf(value.orig) + 1))
        }
    }
    println(form.joinToString("\n") { row ->
        row.joinToString("") {
            if (it == 0) "." else it.toString()
        }
    })
}

private fun addInverse(solver: ISolver) {
    val posVals = solver.primeImplicant().filter { it > 0 }
    solver.addClause(posVals.map { -it }.toVec())
}

fun turnToRight(array: Brick): Brick {
    val resultArray = Array(array[0].size) { IntArray(array.size) }
    for (i in array.indices) {
        for (j in array[i].indices) {
            resultArray[j][array.size - i - 1] = array[i][j]
        }
    }
    return resultArray.map { it.toList() }
}
