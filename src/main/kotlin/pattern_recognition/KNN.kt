package pattern_recognition

val globalData: Set<Item> = setOf(
    listOf(0, 1, 1, 0, 1) itm 1,
    listOf(1, 0, 1, 0, 1) itm 2,
    listOf(1, 1, 0, 1, 1) itm 3,
)

class KNN(private val k: Int, private val dataset: Set<Item>) {

    fun search(input: List<Int>, metric: Metric): Clazz {
        return dataset.associateWith { orig ->
            metric(orig.data, input)
        }.entries.sortedBy { it.value }
            .take(k).groupingBy { it.key.clazz }
            .eachCount().maxByOrNull { it.value }!!.key
    }
}

fun main() {
    repeat(10){
        KNN(1, Noisemaker(globalData).symmetric(0.3f))
            .search(listOf(1, 0, 1, 1, 1), ::euclidean)
            .also { println(it) }
    }
}
