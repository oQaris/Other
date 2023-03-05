package pattern_recognition

import kotlin.random.Random

fun <T> Iterable<T>.sortedCounter(grouping: (T) -> T = { it }) =
    groupingBy(grouping).eachCount().toList().sortedByDescending { it.second }.toMap()

fun Data.noise(p: Float) = map {
    if (Random.nextFloat() < p)
        (it + 1) % 2
    else it
}
