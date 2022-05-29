package tetris

fun generateSatValues(bricks: Collection<Brick>): List<Value> {
    return bricks.flatMap { br ->
        possibleLocations(br)
            .map { Value(it, br) }
    }.toSet().toList()
}

fun zeroBrick() = Array(height) { Array(width) { 0 }.toList() }.toList()

private fun possibleLocations(brick: Brick): Set<Brick> {
    val turns = buildSet {
        var turn = brick
        repeat(4) {
            add(turn)
            turn = turnToRight(turn)
        }
    }
    return buildSet {
        add(zeroBrick()) // можно не ставить фигуру
        turns.forEach { turn ->
            for (i in (0..width - turn.first().size))
                for (j in (0..height - turn.size)) {
                    add(plus(zeroBrick(), turn, i, j))
                }
        }
    }
}

fun plus(form: Brick, brick: Brick, xSift: Int = 0, yShift: Int = 0): Brick {
    require(form.first().size >= brick.first().size + xSift)
    require(form.size >= brick.size + yShift)

    val target = form.map { it.toMutableList() }
    for (i in brick.indices)
        for (j in brick.first().indices)
            target[yShift + i][xSift + j] += brick[i][j]
    return target
}

fun prod(brick: Brick, p: Int): Brick {
    return brick.map { r -> r.map { it * p } }
}

class Value(val slice: Brick, val orig: Brick) : Brick by slice {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Value) return false
        if (slice != other.slice) return false
        return true
    }

    override fun hashCode(): Int {
        return slice.hashCode()
    }
}
