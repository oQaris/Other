package pattern_recognition

val globalData: Set<Item> = setOf(
    listOf(
        0, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 0,
    ) itm 0,
    listOf(
        0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 0, 1, 1, 0, 0, 0,
        0, 0, 1, 0, 1, 0, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 0, 1, 1, 1, 0, 0,
    ) itm 1,
    listOf(
        0, 0, 1, 1, 1, 1, 0, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 0, 1, 0, 0, 0, 0,
        0, 0, 1, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 0,
    ) itm 2,
    listOf(
        0, 0, 1, 1, 1, 1, 0, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 0, 0, 0, 0, 0, 1, 0,
        0, 0, 0, 1, 1, 1, 0, 0,
        0, 0, 0, 0, 0, 0, 1, 0,
        0, 0, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 0, 1, 1, 1, 1, 0, 0,
    ) itm 3,
    listOf(
        0, 1, 0, 0, 0, 1, 0, 0,
        0, 1, 0, 0, 0, 1, 0, 0,
        0, 1, 0, 0, 0, 1, 0, 0,
        0, 1, 0, 0, 0, 1, 0, 0,
        0, 1, 1, 1, 1, 1, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
    ) itm 4,
    listOf(
        0, 0, 1, 1, 1, 1, 1, 0,
        0, 0, 1, 0, 0, 0, 0, 0,
        0, 0, 1, 0, 0, 0, 0, 0,
        0, 0, 1, 1, 1, 0, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 1, 1, 1, 0, 0, 0,
    ) itm 5,
    listOf(
        0, 0, 1, 1, 1, 1, 0, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 0, 0,
        0, 1, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 0, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 0, 1, 1, 1, 1, 0, 0,
    ) itm 6,
    listOf(
        0, 1, 1, 1, 1, 1, 1, 0,
        0, 0, 0, 0, 0, 0, 1, 0,
        0, 0, 0, 0, 0, 0, 1, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0,
    ) itm 7,
    listOf(
        0, 0, 1, 1, 1, 1, 0, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 0, 1, 1, 1, 1, 0, 0,
        0, 0, 1, 0, 0, 1, 0, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 0, 1, 1, 1, 1, 0, 0,
    ) itm 8,
    listOf(
        0, 0, 1, 1, 1, 1, 0, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 0, 1, 1, 1, 1, 1, 0,
        0, 0, 0, 0, 0, 0, 1, 0,
        0, 0, 0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 0, 0, 1, 0,
        0, 0, 1, 1, 1, 1, 0, 0,
    ) itm 9
)

class KNN(private val k: Int, private val dataset: Set<Item>) {

    fun search(input: List<Int>, metric: Metric): Clazz {
        return dataset.associateWith { orig ->
            metric(orig.data, input)
        }.entries.sortedBy { it.value }
            .take(k).groupingBy { it.key.clazz }
            .eachCount().maxByOrNull { it.value }!!.key
    }

    fun neighborhood(input: List<Int>, metric: Metric): List<Item> {
        return dataset.associateWith { orig ->
            metric(orig.data, input)
        }.entries.sortedBy { it.value }
            .take(k).map { it.key }
    }
}

fun main() {
    searchOutliers()
    return
    val iter = 1_000_00
    val knn = KNN(1, globalData)
    val metrics = listOf<Metric>(::manhattan, ::euclidean, ::chebyshev, ::distance, ::cosine)
        .associateWith { mutableMapOf<Clazz, MutableList<Clazz>>() }

    globalData.forEach { orig ->
        repeat(1_000_00) {
            val input = orig.data.noise(0.4f)
            metrics.forEach { (metric, table) ->
                table.putIfAbsent(orig.clazz, mutableListOf())
                table[orig.clazz]!!.add(knn.search(input, metric))
            }
        }
    }

    metrics.forEach { (metric, table) ->
        println(metric.toString().dropLast(37))
        table.mapValues { it.value.sortedCounter() }.entries.forEach { println(it) }
        println()
    }
}

fun expand(data: Set<Item>): Set<Item> {
    return data + (data.map { it.data.noise(0.12f) itm it.clazz }
            + data.map { it.data.noise(0.1f) itm it.clazz }
            + data.map { it.data.noise(0.08f) itm it.clazz }
            + data.map { it.data.noise(0.04f) itm it.clazz })
}

fun getOutliers(data: Set<Item>): Set<Item> {
    return data.shuffled().take(5).map { it.data.noise(0.75f) itm it.clazz }.toSet()
}

fun searchOutliers() {
    val expanded = expand(globalData)
    val outliers = getOutliers(globalData)
    val dataset = (expanded + outliers).shuffled().toSet()
    println("Индексы выбросов:")
    dataset.mapIndexed { index, item ->
        if (item in outliers) index else -1
    }.filter { it > 0 }.also {
        println(it.joinToString(" "))
    }
    println("Распознанные KNN:")
    for (item in dataset) {
        val knn = KNN(3, dataset - item)
        if (knn.search(item.data, ::euclidean) != item.clazz) {
            print("${dataset.indexOf(item)} ")
        }
    }
    println("\nРаспознанные ODIN:")
    val inputs = IntArray(dataset.size)
    for (item in dataset) {
        val knn = KNN(5, dataset - item)
        knn.neighborhood(item.data, ::euclidean).forEach {
            inputs[dataset.indexOf(it)]++
        }
    }
    val t = 3
    inputs.forEachIndexed { index, count ->
        if (count < t) print("$index ")
    }
}
