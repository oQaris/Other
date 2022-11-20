package other.gametheory

fun main() {
    val sh = Schedule(XYTau(mapOf(
        XY(4, 2) to 3,
        XY(4, 5) to 1,
        XY(2, 3) to 4,
        XY(5, 1) to 4,
        XY(3, 1) to 4,
        XY(3, 6) to 4,
        XY(1, 7) to 4,
    )))

    sh.solve()
    sh.genTable().forEach { println(it) }
}